package de.materna.dmn.tester.servlets.model;

import com.fasterxml.jackson.core.type.TypeReference;
import de.materna.dmn.tester.drools.DroolsExecutor;
import de.materna.dmn.tester.drools.helpers.DroolsHelper;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.filters.WriteAccess;
import de.materna.dmn.tester.servlets.input.beans.Decision;
import de.materna.dmn.tester.servlets.model.beans.Model;
import de.materna.dmn.tester.servlets.model.beans.ModelResult;
import de.materna.dmn.tester.servlets.output.beans.Output;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.drools.DroolsAnalyzer;
import de.materna.jdec.drools.DroolsDebugger;
import de.materna.jdec.model.ImportResult;
import de.materna.jdec.model.ModelImportException;
import de.materna.jdec.serialization.SerializationHelper;
import org.apache.log4j.Logger;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.parser.feel11.profiles.KieExtendedFEELProfile;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

@Path("/workspaces/{workspace}")
public class ModelServlet {
	private static final Logger log = Logger.getLogger(ModelServlet.class);

	@GET
	@ReadAccess
	@Path("/model")
	@Produces("application/json")
	public Response getModels(@PathParam("workspace") String workspaceUUID) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

			List<DMNModel> dmnModels = DroolsHelper.getModels(workspace);
			HashMap<String, Model> modelsMap = new HashMap<String, Model>();
			LinkedList<HashMap<String, String>> models = workspace.getConfig().getModels();

			for(int i = 0; i < dmnModels.size(); i++) {
				DMNModel dmnModel = dmnModels.get(i);
				Model model = new Model(dmnModel.getNamespace(), dmnModel.getName(), dmnModel.getDecisions(), dmnModel.getBusinessKnowledgeModels(), dmnModel.getDecisionServices(), workspace.getDecisionSession().getModel(models.get(i).get("namespace"), models.get(i).get("name")));
				modelsMap.put(models.get(i).get("uuid"), model);
			}

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(modelsMap)).build();
		}
		catch (IOException exception) {
			return Response.status(Response.Status.OK).entity("{}").build();
		}
		catch (IndexOutOfBoundsException exception) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Workspace is corrupted.").build();
		}
	}

	@PUT
	@WriteAccess
	@Path("/model")
	@Consumes("application/json")
	public Response importModels(@PathParam("workspace") String workspaceUUID, String body) {
		try {
			//Initialize
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

			HashMap<String, String>[] params = SerializationHelper.getInstance().toClass(body, new TypeReference<LinkedHashMap<String, String>[]>() {});
			ImportResult importResult = null;
			LinkedList<HashMap<String, String>> modelImportOrder = new LinkedList<HashMap<String, String>>();

			//Clear decision session
			workspace.clearDecisionSession();

			//Clear model directory
			workspace.getModelManager().removeAllFiles();

			//Import models, update configuration
			for(int i = 0; i < params.length; i++) {
				HashMap<String, String> modelMap = params[i];

				importResult = workspace.getDecisionSession().importModel(modelMap.get("namespace"), modelMap.get("name"), modelMap.get("source"));

				String modelUUID = UUID.randomUUID().toString();

				workspace.getModelManager().persistFileRaw(modelUUID, modelMap.get("source"));
				modelMap.put("uuid", modelUUID);
				modelMap.remove("source");
				modelImportOrder.add(modelMap);
			}

			workspace.getConfig().setModels(modelImportOrder);

			//Update configuration and access log
			workspace.getConfig().setModifiedDate(System.currentTimeMillis());
			workspace.getConfig().serialize();
			workspace.getAccessLog().writeMessage("Imported models", workspace.getConfig().getModifiedDate());

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(importResult)).build();

		}
		catch (ModelImportException exception) {
			return Response.status(Response.Status.BAD_REQUEST).entity(SerializationHelper.getInstance().toJSON(exception.getResult())).build();
		}
		catch (IOException exception) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@GET
	@ReadAccess
	@Path("/model/inputs")
	@Produces("application/json")
	public Response getInputs(@PathParam("workspace") String workspaceUUID) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		DMNModel model = workspace.getDecisionSession().getRuntime().getModels().get(0);

		workspace.getAccessLog().writeMessage("Accessed list of inputs for model " + model.getName(), System.currentTimeMillis());

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(DroolsAnalyzer.getComplexInputStructure(model))).build();
	}

	@POST
	@ReadAccess
	@Path("/model/execute")
	@Consumes("application/json")
	@Produces("application/json")
	public Response calculateModelResult(@PathParam("workspace") String workspaceUUID, String body) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

			DMNModel dmnModel = DroolsHelper.getModel(workspace);

			Map<String, Object> inputs = SerializationHelper.getInstance().toClass(body, new TypeReference<HashMap<String, Object>>() {
			});

			ExecutionResult executionResult = workspace.getDecisionSession().executeModel(dmnModel, inputs);
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(executionResult)).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@POST
	@ReadAccess
	@Path("/model/execute/raw")
	@Consumes("application/json")
	@Produces("text/plain")
	public Response calculateRawResult(String body) {
		Decision decision = (Decision) SerializationHelper.getInstance().toClass(body, Decision.class);

		try {
			List<FEELProfile> profiles = new ArrayList<>();
			profiles.add(new KieExtendedFEELProfile());
			FEEL feel = FEEL.newInstance(profiles);

			List<String> messages = new LinkedList<>();
			feel.addListener(feelEvent -> messages.add(feelEvent.getMessage()));

			HashMap<String, Object> outputs = new LinkedHashMap<>();
			outputs.put("main", DroolsHelper.cleanResult(feel.evaluate(decision.getExpression(), decision.getContext())));

			ExecutionResult modelResult = new ExecutionResult(outputs, null, messages);
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(modelResult)).build();
		}
		catch (Exception exception) {
			log.error(exception);

			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
}