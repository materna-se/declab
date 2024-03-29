package de.materna.dmn.tester.servlets.challenges;

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

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

@Path("/workspaces/{workspace}/challenges")
public class ChallengeServlet {
	@GET
	@ReadAccess
	@Produces("application/json")
	public Response getChallenges(@PathParam("workspace") String workspaceUUID, @QueryParam("order") boolean order) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		PersistenceDirectoryManager<Challenge> challengeManager = workspace.getChallengeManager();

		Map<String, Challenge> unsortedChallenges = challengeManager.getFiles();

		Map<String, Challenge> sortedChallenges = new LinkedHashMap<>();
		unsortedChallenges.entrySet().stream().sorted(Map.Entry.comparingByValue((o1, o2) -> (order ? -1 : 1) * o1.getName().compareTo(o2.getName()))).forEach(entry -> sortedChallenges.put(entry.getKey(), entry.getValue()));

		return Response.status(Response.Status.OK).entity(sortedChallenges).build();
	}

	@GET
	@ReadAccess
	@Path("/{uuid}")
	@Produces("application/json")
	public Response getChallenge(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String challengeUUID, @QueryParam("merge") boolean merge) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		PersistenceDirectoryManager<Challenge> challengeManager = workspace.getChallengeManager();

		Challenge challenge = challengeManager.getFile(challengeUUID);
		if (challenge == null) {
			throw new NotFoundException();
		}

		workspace.getAccessLog().writeMessage("Accessed challenge " + challengeUUID, System.currentTimeMillis());

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(challenge)).build();
	}

	private Challenge calculateChallengeScenarios(Challenge challenge) throws ModelNotFoundException, ModelImportException {
		// This method is used to calculate the scenario outputs of a challenge from its inputs and its solution
		ArrayList<Scenario> scenarios = (ArrayList<Scenario>) challenge.getScenarios();

		ChallengeType challengeType = challenge.getType();

		// Slightly different depending on challenge type
		if (challengeType.equals(ChallengeType.FEEL)) {
			String feelExpression = (String) challenge.getSolution();

			scenarios = ChallengeExecutionHelper.calculateFEELExpression(feelExpression, scenarios);
			challenge.setScenarios(scenarios);
		}
		else if (challengeType.equals(ChallengeType.DMN_MODEL)) {
			// Need to re-parse to avoid ClassCastException
			DMNSolution solution = (DMNSolution) SerializationHelper.getInstance().toClass(SerializationHelper.getInstance().toJSON(challenge.getSolution()), DMNSolution.class);

			ArrayList<ModelMap> modelMaps = solution.getModels();

			DecisionService decisionService = solution.getDecisionService();

			// Use decision service if available
			if (decisionService != null) {
				scenarios = ChallengeExecutionHelper.calculateModels(modelMaps, scenarios, decisionService);
			}
			else {
				scenarios = ChallengeExecutionHelper.calculateModels(modelMaps, scenarios);
			}

			challenge.setScenarios(scenarios);
		}

		return challenge;
	}

	@POST
	@WriteAccess
	@Consumes("application/json")
	public Response createChallenge(@PathParam("workspace") String workspaceUUID, String body) throws IOException, RuntimeException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		String uuid = UUID.randomUUID().toString();

		try {
			Challenge challenge = (Challenge) SerializationHelper.getInstance().toClass(body, Challenge.class);

			challenge = calculateChallengeScenarios(challenge);

			workspace.getChallengeManager().persistFile(uuid, challenge);

			workspace.getAccessLog().writeMessage("Created challenge" + uuid, System.currentTimeMillis());

			return Response.status(Response.Status.CREATED).entity(uuid).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	@PUT
	@WriteAccess
	@Path("/{uuid}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response editChallenge(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String challengeUUID, String body) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		PersistenceDirectoryManager<Challenge> challengeManager = workspace.getChallengeManager();

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
	public Response deleteChallenge(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String challengeUUID) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		PersistenceDirectoryManager<Challenge> challengeManager = workspace.getChallengeManager();

		Challenge challenge = challengeManager.getFile(challengeUUID);
		if (challenge == null) {
			throw new NotFoundException(String.format("Can't find challenge with uuid %s.", challengeUUID));
		}

		challengeManager.removeFile(challengeUUID);

		workspace.getAccessLog().writeMessage("Deleted challenge " + challengeUUID, System.currentTimeMillis());

		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@POST
	@ReadAccess
	@Path("/execute_dmn_list")
	@Consumes("application/json")
	@Produces("application/json")
	public Response calculateModelChallengeList(@PathParam("workspace") String workspaceUUID, String body) throws IOException {
		// Calculate a list of scenario outputs using a list of models to import
		// It's important to calculate all scenarios in one request because the overhead of
		// importing all models for each scenario is significant

		try {
			Map<String, Object> params = SerializationHelper.getInstance().toClass(body, new TypeReference<HashMap<String, Object>>() {
			});

			if (params.get("models") == null || params.get("inputs") == null) {
				throw new IllegalArgumentException();
			}

			DMNDecisionSession dS = new DMNDecisionSession();

			LinkedList<ExecutionResult> results = new LinkedList<ExecutionResult>();

			// Import models (defined by name (unused), namespace and source XML)
			ArrayList<Map<String, Object>> modelMaps = (ArrayList<Map<String, Object>>) params.get("models");

			for (Map<String, Object> modelMap : modelMaps) {
				String modelNamespace = (String) modelMap.get("namespace");
				String modelSource = (String) modelMap.get("source");

				dS.importModel(modelNamespace, modelSource);
			}

			// Get parsed models from decision session
			List<Model> models = dS.getModels();

			// Get scenario inputs sent by client
			ArrayList<Map<String, Object>> inputs = (ArrayList<Map<String, Object>>) params.get("inputs");

			// Execute each scenario and store its results

			DecisionService decisionService = (DecisionService) SerializationHelper.getInstance().toClass(SerializationHelper.getInstance().toJSON(params.get("decisionService")), DecisionService.class);

			for (Map<String, Object> input : inputs) {
				ExecutionResult result;

				if (decisionService == null || decisionService.getName() == null || decisionService.getName().equals("")) {
					String ns = models.get(models.size() - 1).getNamespace();
					result = dS.executeModel(ns, input);
				}
				else {
					result = dS.executeModel(decisionService.getNamespace(), decisionService.getName(), input);
				}

				results.add(result);
			}

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(results)).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
}
