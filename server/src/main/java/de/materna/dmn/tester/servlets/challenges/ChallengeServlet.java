package de.materna.dmn.tester.servlets.challenges;

import java.io.IOException;
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

import de.materna.dmn.tester.persistence.PersistenceDirectoryManager;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.challenges.beans.Challenge;
import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.filters.WriteAccess;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
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

	@POST
	@WriteAccess
	@Path("/challenges")
	@Consumes("application/json")
	public Response createChallenge(@PathParam("workspace") String workspaceUUID, String body) throws IOException, RuntimeException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		String uuid = UUID.randomUUID().toString();
		
		Challenge bean = (Challenge) SerializationHelper.getInstance().toClass(body, Challenge.class);

		workspace.getChallengeManager().persistFile(uuid, bean);

		workspace.getAccessLog().writeMessage("Created challenge" + uuid, System.currentTimeMillis());

		return Response.status(Response.Status.CREATED).entity(uuid).build();
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

		Challenge challenge = (Challenge) SerializationHelper.getInstance().toClass(body, Challenge.class);
		
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
