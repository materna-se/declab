package de.materna.dmn.tester.servlets.challenges;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.type.TypeReference;

import de.materna.dmn.tester.persistence.PersistenceDirectoryManager;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.challenges.beans.Challenge;
import de.materna.dmn.tester.servlets.challenges.beans.Challenge.ChallengeType;
import de.materna.dmn.tester.servlets.challenges.beans.DMNSolution;
import de.materna.dmn.tester.servlets.challenges.beans.ModelMap;
import de.materna.dmn.tester.servlets.challenges.beans.Scenario;
import de.materna.dmn.tester.servlets.challenges.helpers.ChallengeExecutionHelper;
import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.filters.WriteAccess;
import de.materna.dmn.tester.servlets.workspace.beans.Configuration.DecisionService;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.DMNDecisionSession;
import de.materna.jdec.model.ExecutionResult;
import de.materna.jdec.model.Model;
import de.materna.jdec.model.ModelImportException;
import de.materna.jdec.model.ModelNotFoundException;
import de.materna.jdec.serialization.SerializationHelper;

@Path("/workspaces/{workspace}/challenges")
public class ChallengeServlet {
	@GET
	@ReadAccess
	@Produces("application/json")
	public Response getChallenges(@PathParam("workspace") String workspaceUUID, @QueryParam("order") boolean order)
			throws IOException {
		final Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		final PersistenceDirectoryManager<Challenge> challengeManager = workspace.getChallengeManager();

		final Map<String, Challenge> unsortedChallenges = challengeManager.getFiles();

		final Map<String, Challenge> sortedChallenges = new LinkedHashMap<>();
		unsortedChallenges.entrySet().stream()
				.sorted(Map.Entry.comparingByValue((o1, o2) -> (order ? -1 : 1) * o1.getName().compareTo(o2.getName())))
				.forEach(entry -> sortedChallenges.put(entry.getKey(), entry.getValue()));

		return Response.status(Response.Status.OK).entity(sortedChallenges).build();
	}

	@GET
	@ReadAccess
	@Path("/{uuid}")
	@Produces("application/json")
	public Response getChallenge(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String challengeUUID,
			@QueryParam("merge") boolean merge) throws IOException {
		final Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		final PersistenceDirectoryManager<Challenge> challengeManager = workspace.getChallengeManager();

		final Challenge challenge = challengeManager.getFile(challengeUUID);
		if (challenge == null) {
			throw new NotFoundException();
		}

		workspace.getAccessLog().writeMessage("Accessed challenge " + challengeUUID, System.currentTimeMillis());

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(challenge)).build();
	}

	private Challenge calculateChallengeScenarios(Challenge challenge)
			throws ModelNotFoundException, ModelImportException {
		// This method is used to calculate the scenario outputs of a challenge from its
		// inputs and its solution
		ArrayList<Scenario> scenarios = (ArrayList<Scenario>) challenge.getScenarios();

		final ChallengeType challengeType = challenge.getType();

		// Slightly different depending on challenge type
		if (ChallengeType.FEEL.equals(challengeType)) {
			final String feelExpression = (String) challenge.getSolution();

			scenarios = ChallengeExecutionHelper.calculateFEELExpression(feelExpression, scenarios);
			challenge.setScenarios(scenarios);
		} else if (ChallengeType.DMN_MODEL.equals(challengeType)) {
			// Need to re-parse to avoid ClassCastException
			final DMNSolution solution = (DMNSolution) SerializationHelper.getInstance()
					.toClass(SerializationHelper.getInstance().toJSON(challenge.getSolution()), DMNSolution.class);

			final ArrayList<ModelMap> modelMaps = solution.getModels();

			final DecisionService decisionService = solution.getDecisionService();

			// Use decision service if available
			if (decisionService != null) {
				scenarios = ChallengeExecutionHelper.calculateModels(modelMaps, scenarios, decisionService);
			} else {
				scenarios = ChallengeExecutionHelper.calculateModels(modelMaps, scenarios);
			}

			challenge.setScenarios(scenarios);
		}

		return challenge;
	}

	@POST
	@WriteAccess
	@Consumes("application/json")
	public Response createChallenge(@PathParam("workspace") String workspaceUUID, String body)
			throws IOException, RuntimeException {
		final Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		final String uuid = UUID.randomUUID().toString();

		try {
			Challenge challenge = (Challenge) SerializationHelper.getInstance().toClass(body, Challenge.class);

			challenge = calculateChallengeScenarios(challenge);

			workspace.getChallengeManager().persistFile(uuid, challenge);

			workspace.getAccessLog().writeMessage("Created challenge" + uuid, System.currentTimeMillis());

			return Response.status(Response.Status.CREATED).entity(uuid).build();
		} catch (final Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	@PUT
	@WriteAccess
	@Path("/{uuid}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response editChallenge(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String challengeUUID,
			String body) throws IOException {
		final Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		final PersistenceDirectoryManager<Challenge> challengeManager = workspace.getChallengeManager();

		if (!challengeManager.getFiles().containsKey(challengeUUID)) {
			throw new NotFoundException();
		}

		Challenge challenge = (Challenge) SerializationHelper.getInstance().toClass(body, Challenge.class);

		challenge = calculateChallengeScenarios(challenge);

		challengeManager.persistFile(challengeUUID, challenge);

		workspace.getAccessLog().writeMessage("Edited challenge " + challengeUUID, System.currentTimeMillis());

		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@DELETE
	@WriteAccess
	@Path("/{uuid}")
	public Response deleteChallenge(@PathParam("workspace") String workspaceUUID,
			@PathParam("uuid") String challengeUUID) throws IOException {
		final Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		final PersistenceDirectoryManager<Challenge> challengeManager = workspace.getChallengeManager();

		final Challenge challenge = challengeManager.getFile(challengeUUID);
		if (challenge == null) {
			throw new NotFoundException(String.format("Can't find challenge with uuid %s.", challengeUUID));
		}

		challengeManager.removeFile(challengeUUID);

		workspace.getAccessLog().writeMessage("Deleted challenge " + challengeUUID, System.currentTimeMillis());

		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@SuppressWarnings("unchecked")
	@POST
	@ReadAccess
	@Path("/execute_dmn_list")
	@Consumes("application/json")
	@Produces("application/json")
	public Response calculateModelChallengeList(@PathParam("workspace") String workspaceUUID, String body)
			throws IOException {
		// Calculate a list of scenario outputs using a list of models to import
		// It's important to calculate all scenarios in one request because the overhead
		// of
		// importing all models for each scenario is significant

		try {
			final Map<String, Object> params = SerializationHelper.getInstance().toClass(body,
					new TypeReference<HashMap<String, Object>>() {
					});

			if (params.get("models") == null || params.get("inputs") == null) {
				throw new IllegalArgumentException();
			}

			final DMNDecisionSession dS = new DMNDecisionSession();

			final LinkedList<ExecutionResult> results = new LinkedList<>();

			// Import models (defined by name (unused), namespace and source XML)
			final ArrayList<Map<String, Object>> modelMaps = (ArrayList<Map<String, Object>>) params.get("models");

			for (final Map<String, Object> modelMap : modelMaps) {
				final String modelNamespace = (String) modelMap.get("namespace");
				final String modelSource = (String) modelMap.get("source");

				dS.importModel(modelNamespace, modelSource);
			}

			// Get parsed models from decision session
			final List<Model> models = dS.getModels();

			// Get scenario inputs sent by client
			final ArrayList<Map<String, Object>> inputs = (ArrayList<Map<String, Object>>) params.get("inputs");

			// Execute each scenario and store its results

			final DecisionService decisionService = (DecisionService) SerializationHelper.getInstance().toClass(
					SerializationHelper.getInstance().toJSON(params.get("decisionService")), DecisionService.class);

			for (final Map<String, Object> input : inputs) {
				ExecutionResult result;

				if (decisionService == null || decisionService.getName() == null
						|| "".equals(decisionService.getName())) {
					final String ns = models.get(models.size() - 1).getNamespace();
					result = dS.executeModel(ns, input);
				} else {
					result = dS.executeModel(decisionService.getNamespace(), decisionService.getName(), input);
				}

				results.add(result);
			}

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(results))
					.build();
		} catch (final Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
}
