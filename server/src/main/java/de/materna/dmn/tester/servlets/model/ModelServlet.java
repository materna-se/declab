package de.materna.dmn.tester.servlets.model;

import com.fasterxml.jackson.core.type.TypeReference;
import de.materna.dmn.tester.drools.helpers.DroolsHelper;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.filters.WriteAccess;
import de.materna.dmn.tester.servlets.input.beans.Decision;
import de.materna.dmn.tester.servlets.workspace.beans.Configuration;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.dmn.tester.sockets.managers.SessionManager;
import de.materna.jdec.HybridDecisionSession;
import de.materna.jdec.model.ExecutionResult;
import de.materna.jdec.model.ImportResult;
import de.materna.jdec.model.ModelImportException;
import de.materna.jdec.serialization.SerializationHelper;
import org.apache.log4j.Logger;

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
	public Response getModels(@PathParam("workspace") String workspaceUUID) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(workspace.getDecisionSession().getModels())).build();
	}

	@PUT
	@WriteAccess
	@Path("/model")
	@Consumes("application/json")
	public Response importModels(@PathParam("workspace") String workspaceUUID, String body) throws Exception {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		List<Map<String, String>> models = SerializationHelper.getInstance().toClass(body, new TypeReference<LinkedList<Map<String, String>>>() {
		});

		// Save current decision models so we can rollback if the import fails.
		Map<String, String> currentFiles = workspace.getModelManager().getFiles();

		// Clear decision session and the corresponding directory.
		workspace.getModelManager().removeAllFiles();
		workspace.clearDecisionSession();

		try {
			ImportResult importResult = new ImportResult();

			List<Map<String, String>> importedModels = new LinkedList<>();
			// Import the provided models, collect all import messages.
			for (Map<String, String> model : models) {
				importResult.getMessages().addAll(workspace.getDecisionSession().importModel(model.get("namespace"), model.get("source")).getMessages());

				String uuid = UUID.randomUUID().toString();
				workspace.getModelManager().persistFile(uuid, model.get("source"));
				model.put("uuid", uuid);
				model.remove("source");

				importedModels.add(model);
			}
			Configuration configuration = workspace.getConfig();
			configuration.setModels(importedModels);

			// Check if the configured decision service still exists.
			Configuration.DecisionService decisionService = configuration.getDecisionService();
			if (decisionService != null) {
				if (configuration.getModels().size() == 0 || workspace.getDecisionSession().getModel(DroolsHelper.getMainModelNamespace(workspace)).getDecisionServices().stream().noneMatch(name -> name.equals(decisionService.getName()))) {
					// If the decision service does not exist anymore, we will remove the reference from the configuration.
					configuration.setDecisionService(null);
				}
			}

			// Update the configuration and add an access log entry.
			configuration.setModifiedDate(System.currentTimeMillis());
			configuration.serialize();

			workspace.getAccessLog().writeMessage("Imported models", configuration.getModifiedDate());

			// Notify all sessions.
			SessionManager.getInstance().notify(workspaceUUID, "{\"type\": \"imported\"}");

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(importResult)).build();

		}
		catch (ModelImportException exception) {
			for (Map.Entry<String, String> entry : currentFiles.entrySet()) {
				workspace.getModelManager().persistFile(entry.getKey(), entry.getValue());
			}
			DroolsHelper.importModels(workspace);

			return Response.status(Response.Status.BAD_REQUEST).entity(SerializationHelper.getInstance().toJSON(exception.getResult())).build();
		}
	}

	@GET
	@ReadAccess
	@Produces("application/json")
	@Path("/model/decision-session")
	public Response getDecisionSession(@PathParam("workspace") String workspaceUUID, String body) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(workspace.getConfig().getDecisionService())).build();
	}

	@PUT
	@WriteAccess
	@Consumes("application/json")
	@Path("/model/decision-session")
	public Response setDecisionSession(@PathParam("workspace") String workspaceUUID, String body) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		Configuration configuration = workspace.getConfig();
		configuration.setDecisionService((Configuration.DecisionService) SerializationHelper.getInstance().toClass(body, Configuration.DecisionService.class));
		configuration.serialize();

		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@GET
	@ReadAccess
	@Path("/model/inputs")
	@Produces("application/json")
	public Response getInputs(@PathParam("workspace") String workspaceUUID) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		Configuration configuration = workspace.getConfig();
		String mainModelNamespace = DroolsHelper.getMainModelNamespace(workspace);

		if (configuration.getDecisionService() == null) {
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(workspace.getDecisionSession().getInputStructure(mainModelNamespace))).build();
		}

		// TODO: Throw error if a decision service is selected on a Java decision model.
		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(workspace.getDecisionSession().getDMNDecisionSession().getInputStructure(mainModelNamespace, configuration.getDecisionService().getName()))).build();
	}

	@POST
	@ReadAccess
	@Path("/model/execute")
	@Consumes("application/json")
	@Produces("application/json")
	public Response calculateModelResult(@PathParam("workspace") String workspaceUUID, String body) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		Map<String, Object> inputs = SerializationHelper.getInstance().toClass(body, new TypeReference<HashMap<String, Object>>() {
		});

		Configuration configuration = workspace.getConfig();
		String mainModelNamespace = DroolsHelper.getMainModelNamespace(workspace);

		if (configuration.getDecisionService() == null) {
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(workspace.getDecisionSession().executeModel(mainModelNamespace, inputs))).build();
		}

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(workspace.getDecisionSession().getDMNDecisionSession().executeModel(mainModelNamespace, configuration.getDecisionService().getName(), inputs))).build();
	}

	@POST
	@ReadAccess
	@Path("/model/execute/raw")
	@Consumes("application/json")
	@Produces("text/plain")
	public Response calculateRawResult(@PathParam("workspace") String workspaceUUID, String body) throws IOException {
		Decision decision = (Decision) SerializationHelper.getInstance().toClass(body, Decision.class);

		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		HybridDecisionSession decisionSession = workspace.getDecisionSession();
		try {
			ExecutionResult executionResult = decisionSession.getDMNDecisionSession().executeExpression(decision.getExpression(), decision.getContext());
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(executionResult)).build();
		}
		catch (ModelImportException exception) {
			return Response.status(Response.Status.BAD_REQUEST).entity(SerializationHelper.getInstance().toJSON(exception.getResult())).build();
		}
	}
}