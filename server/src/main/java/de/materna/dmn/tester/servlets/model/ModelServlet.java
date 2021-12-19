package de.materna.dmn.tester.servlets.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.ast.DecisionNode;

import com.fasterxml.jackson.core.type.TypeReference;

import de.materna.dmn.tester.drools.helpers.DroolsHelper;
import de.materna.dmn.tester.helpers.SynchronizationHelper;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.filters.WriteAccess;
import de.materna.dmn.tester.servlets.input.beans.Decision;
import de.materna.dmn.tester.servlets.workspace.beans.Configuration;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.dmn.tester.sockets.managers.SessionManager;
import de.materna.jdec.CamundaDecisionSession;
import de.materna.jdec.DMNDecisionSession;
import de.materna.jdec.GoldmanDecisionSession;
import de.materna.jdec.model.ExecutionResult;
import de.materna.jdec.model.ImportResult;
import de.materna.jdec.model.InputStructure;
import de.materna.jdec.model.Model;
import de.materna.jdec.model.ModelImportException;
import de.materna.jdec.model.ModelNotFoundException;
import de.materna.jdec.serialization.SerializationHelper;

@Path("/workspaces/{workspace}/model")
public class ModelServlet {
	@GET
	@ReadAccess
	@Produces("application/json")
	public Response getModels(@PathParam("workspace") String workspaceUUID) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		List<Model> models = new LinkedList<>();

		for (Map<String, String> model : workspace.getConfig().getModels()) {
			String namespace = model.get("namespace");
			try {
				models.add(workspace.getDecisionSession().getModel(namespace));
			} catch (ModelNotFoundException e) {
				String source = workspace.getModelManager().getFile(model.get("uuid")); // TODO: IOException?
				models.add(new Model(namespace, model.get("name"), source, Collections.emptySet(),
						Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), false));
			}
		}

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(models)).build();
	}

	@PUT
	@WriteAccess
	@Consumes("application/json")
	public Response importModels(@PathParam("workspace") String workspaceUUID, @QueryParam("context") String context,
			String body) throws Exception {
		SynchronizationHelper.getWorkspaceLock(workspaceUUID).writeLock().lock();

		try {
			List<Map<String, String>> models = SerializationHelper.getInstance().toClass(body,
					new TypeReference<LinkedList<Map<String, String>>>() {
					});

			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

			// Remove all old models and clear the decision session.
			workspace.getModelManager().removeAllFiles();
			workspace.clearDecisionSession();

			boolean successful = true;

			ImportResult globalImportResult = new ImportResult();

			List<Map<String, String>> importedModels = new LinkedList<>();
			// Import the provided models, collect all import messages.
			for (Map<String, String> model : models) {
				try {
					ImportResult importResult = workspace.getDecisionSession().importModel(model.get("namespace"),
							model.get("source"));
					globalImportResult.getMessages().addAll(importResult.getMessages());
				} catch (ModelImportException exception) {
					successful = false;
					globalImportResult.getMessages().addAll(exception.getResult().getMessages());
				}

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
				if (configuration.getModels().size() == 0 || workspace.getDecisionSession()
						.getModel(DroolsHelper.getMainModelNamespace(workspace)).getDecisionServices().stream()
						.noneMatch(name -> name.equals(decisionService.getName()))) {
					// If the decision service does not exist anymore, we will remove the reference
					// from the configuration.
					configuration.setDecisionService(null);
				}
			}

			// Update the configuration and add an access log entry.
			configuration.setModifiedDate(System.currentTimeMillis());
			configuration.serialize();

			workspace.getAccessLog().writeMessage("Imported models", configuration.getModifiedDate());

			// Notify all sessions.
			SessionManager.getInstance().notify(workspaceUUID,
					"{\"type\": \"imported\", \"data\": " + SerializationHelper.getInstance().toJSON(context) + "}");

			return Response.status(successful ? Response.Status.OK : Response.Status.BAD_REQUEST)
					.entity(SerializationHelper.getInstance().toJSON(globalImportResult)).build();
		} finally {
			SynchronizationHelper.getWorkspaceLock(workspaceUUID).writeLock().unlock();
		}
	}

	@GET
	@ReadAccess
	@Produces("application/json")
	@Path("/decision-session")
	public Response getDecisionSession(@PathParam("workspace") String workspaceUUID) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		return Response.status(Response.Status.OK)
				.entity(SerializationHelper.getInstance().toJSON(workspace.getConfig().getDecisionService())).build();
	}

	@PUT
	@WriteAccess
	@Consumes("application/json")
	@Path("/decision-session")
	public Response setDecisionSession(@PathParam("workspace") String workspaceUUID, String body) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		Configuration configuration = workspace.getConfig();
		configuration.setDecisionService((Configuration.DecisionService) SerializationHelper.getInstance().toClass(body,
				Configuration.DecisionService.class));
		configuration.serialize();

		// Notify all sessions.
		SessionManager.getInstance().notify(workspaceUUID, "{\"type\": \"imported\"}");

		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@GET
	@ReadAccess
	@Path("/inputs")
	@Produces("application/json")
	public Response getInputs(@PathParam("workspace") String workspaceUUID) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		Configuration configuration = workspace.getConfig();
		DMNModel mainModel = DroolsHelper.getMainModel(workspace);

		Map<String, Object> context = new HashMap<>();
		if (configuration.getDecisionService() == null) {
			context.put("inputs", workspace.getDecisionSession().getInputStructure(mainModel.getNamespace()));

			Map<String, InputStructure> decisions = new HashMap<>();
			for (DecisionNode decision : mainModel.getDecisions()) {
				decisions.put(decision.getName(), new InputStructure(decision.getResultType().getName()));
			}
			context.put("decisions", decisions);
		} else {
			context.put("inputs", workspace.getDecisionSession().getDMNDecisionSession()
					.getInputStructure(mainModel.getNamespace(), configuration.getDecisionService().getName()));
			context.put("decisions", Collections.emptyMap());
		}

		// TODO: Throw error if a decision service is selected on a Java decision model.
		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(context)).build();
	}

	@POST
	@ReadAccess
	@Path("/execute")
	@Consumes("application/json")
	@Produces("application/json")
	public Response calculateModelResult(@PathParam("workspace") String workspaceUUID, String body) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		Map<String, Object> inputs = SerializationHelper.getInstance().toClass(body,
				new TypeReference<HashMap<String, Object>>() {
				});

		Configuration configuration = workspace.getConfig();
		String mainModelNamespace = DroolsHelper.getMainModelNamespace(workspace);

		if (configuration.getDecisionService() == null) {
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance()
					.toJSON(workspace.getDecisionSession().executeModel(mainModelNamespace, inputs))).build();
		}

		return Response.status(Response.Status.OK)
				.entity(SerializationHelper.getInstance().toJSON(workspace.getDecisionSession().getDMNDecisionSession()
						.executeModel(mainModelNamespace, configuration.getDecisionService().getName(), inputs)))
				.build();
	}

	@POST
	@ReadAccess
	@Path("/execute/raw")
	@Consumes("application/json")
	@Produces("text/plain")
	public Response calculateRawResult(@PathParam("workspace") String workspaceUUID,
			@QueryParam("engine") String engine, String body) throws IOException {
		Decision decision = (Decision) SerializationHelper.getInstance().toClass(body, Decision.class);

		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		try {
			ExecutionResult executionResult;
			switch (engine) {
			case "DROOLS":
			default:
				executionResult = workspace.getDecisionSession().getDMNDecisionSession()
						.executeExpression(decision.getExpression(), decision.getContext());
				break;
			case "CAMUNDA":
				executionResult = new CamundaDecisionSession().executeExpression(decision.getExpression(),
						decision.getContext());
				break;
			case "GOLDMAN":
				executionResult = new GoldmanDecisionSession().executeExpression(decision.getExpression(),
						decision.getContext());
				break;
			}
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(executionResult))
					.build();
		} catch (ModelImportException exception) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(SerializationHelper.getInstance().toJSON(exception.getResult())).build();
		}
	}

	@POST
	@ReadAccess
	@Path("/model/anonymous")
	@Consumes("application/json")
	@Produces("application/json")
	public Response importModelsAnonymous(@PathParam("workspace") String workspaceUUID, String body)
			throws IOException {
		List<Map<String, String>> models = SerializationHelper.getInstance().toClass(body,
				new TypeReference<LinkedList<Map<String, String>>>() {
				});

		DMNDecisionSession ds = new DMNDecisionSession();

		try {
			ImportResult importResult = new ImportResult();

			List<Map<String, String>> importedModels = new LinkedList<>();
			// Import the provided models, collect all import messages.
			for (Map<String, String> model : models) {
				importResult.getMessages()
						.addAll(ds.importModel(model.get("namespace"), model.get("source")).getMessages());
				importedModels.add(model);
			}

			HashMap<String, Object> responseMap = new HashMap<String, Object>();

			responseMap.put("results", importResult);
			responseMap.put("models", ds.getModels());

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(responseMap))
					.build();
		} catch (ModelImportException exception) {
			HashMap<String, Object> responseMap = new HashMap<String, Object>();

			responseMap.put("results", exception.getResult());
			responseMap.put("models", new ArrayList<Model>());

			return Response.status(Response.Status.BAD_REQUEST)
					.entity(SerializationHelper.getInstance().toJSON(responseMap)).build();
		}
	}
}
