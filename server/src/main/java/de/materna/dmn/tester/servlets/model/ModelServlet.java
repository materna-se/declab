package de.materna.dmn.tester.servlets.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import de.materna.jdec.model.ModelNotFoundException;
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
	public Response getModel(@PathParam("workspace") String workspaceUUID) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

			DMNModel dmnModel = DroolsHelper.getModel(workspace);

			Model model = new Model(dmnModel.getName(), dmnModel.getDecisions(), dmnModel.getBusinessKnowledgeModels(), dmnModel.getDecisionServices());

			workspace.getAccessLog().writeMessage("Accessed model " + model.getName(), System.currentTimeMillis());

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(model)).build();
		}
		catch (ModelNotFoundException exception) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@PUT
	@WriteAccess
	@Path("/model")
	@Consumes("text/xml")
	public Response importModel(@PathParam("workspace") String workspaceUUID, String body) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

			ImportResult importResult = workspace.getDecisionSession().importModel("main", "main", body);

			workspace.getModelManager().persistFile(body);

			workspace.getAccessLog().writeMessage("Imported model", System.currentTimeMillis());

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
	public Response getInputs(@PathParam("workspace") String workspaceUUID) {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		DMNModel model = workspace.getDecisionSession().getRuntime().getModels().get(0);

		workspace.getAccessLog().writeMessage("Accessed list of inputs for model " + model.getName(), System.currentTimeMillis());

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(DroolsAnalyzer.getInputs(model))).build();
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

			DroolsDebugger debugger = new DroolsDebugger(workspace.getDecisionSession());
			debugger.start();
			Map<String, Output> outputs = DroolsExecutor.getOutputs(workspace.getDecisionSession(), dmnModel, inputs);
			debugger.stop();

			workspace.getAccessLog().writeMessage("Calculated result for model", System.currentTimeMillis());

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(new ModelResult(outputs, debugger.getDecisions(), debugger.getMessages()))).build();
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

			HashMap<String, Output> decisions = new LinkedHashMap<>();
			ObjectMapper objectMapper = SerializationHelper.getInstance().getJSONMapper();
			decisions.put("main", new Output(objectMapper.valueToTree(de.materna.jdec.drools.DroolsHelper.cleanResult(feel.evaluate(decision.getExpression(), decision.getContext())))));

			ModelResult modelResult = new ModelResult(decisions, null, messages);
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(modelResult)).build();
		}
		catch (Exception exception) {
			log.error(exception);

			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
}