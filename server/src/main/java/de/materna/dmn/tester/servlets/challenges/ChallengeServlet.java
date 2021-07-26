package de.materna.dmn.tester.servlets.challenges;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import de.materna.jdec.model.ModelImportException;
import de.materna.jdec.model.ModelNotFoundException;
import de.materna.jdec.serialization.SerializationHelper;

@Path("/workspaces/{workspace}")
public class ChallengeServlet {
	@GET
	@ReadAccess
	@Path("/challenges")
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
	@Path("/challenges/{uuid}")
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
	
	private Challenge calculateChallengeScenarios(Challenge challenge, Map<String, Object> params) throws ModelNotFoundException, ModelImportException {
		// This method is used to calculate the scenario outputs of a challenge from its inputs and its solution
		ArrayList<Scenario> scenarios = (ArrayList<Scenario>) challenge.getScenarios();
		
		ChallengeType challengeType = challenge.getType();
		
		// Slightly different depending on challenge type
		if (challengeType.equals(ChallengeType.FEEL)) {
			String feelExpression = (String) params.get("solution");
			
			scenarios = ChallengeExecutionHelper.calculateFEELExpression(feelExpression, scenarios);
			challenge.setScenarios(scenarios);
		} else if (challengeType.equals(ChallengeType.DMN_MODEL)) {
			DMNSolution solution = (DMNSolution) SerializationHelper.getInstance().toClass(SerializationHelper.getInstance().toJSON(params.get("solution")), DMNSolution.class);
			
			ArrayList<ModelMap> modelMaps = solution.getModels();
			
			DecisionService decisionService = (DecisionService) solution.getDecisionService();
			
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
	@Path("/challenges")
	@Consumes("application/json")
	public Response createChallenge(@PathParam("workspace") String workspaceUUID, String body) throws IOException, RuntimeException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		String uuid = UUID.randomUUID().toString();
		
		try {
			Map<String, Object> params = SerializationHelper.getInstance().toClass(body, new TypeReference<HashMap<String, Object>>() {
			});
			
			Challenge challenge = (Challenge) SerializationHelper.getInstance().toClass(body, Challenge.class);
			
			challenge = calculateChallengeScenarios(challenge, params);
			
			workspace.getChallengeManager().persistFile(uuid, challenge);

			workspace.getAccessLog().writeMessage("Created challenge" + uuid, System.currentTimeMillis());

			return Response.status(Response.Status.CREATED).entity(uuid).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	@PUT
	@WriteAccess
	@Path("/challenges/{uuid}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response editChallenge(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String challengeUUID, String body) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		PersistenceDirectoryManager<Challenge> challengeManager = workspace.getChallengeManager();
		
		if (!challengeManager.getFiles().containsKey(challengeUUID)) {
			throw new NotFoundException();
		}

		Map<String, Object> params = SerializationHelper.getInstance().toClass(body, new TypeReference<HashMap<String, Object>>() {
		});
		
		Challenge challenge = (Challenge) SerializationHelper.getInstance().toClass(body, Challenge.class);
		
		challenge = calculateChallengeScenarios(challenge, params);
		
		challengeManager.persistFile(challengeUUID, challenge);

		workspace.getAccessLog().writeMessage("Edited challenge " + challengeUUID, System.currentTimeMillis());

		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@DELETE
	@WriteAccess
	@Path("/challenges/{uuid}")
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
	
	
}
