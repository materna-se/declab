package de.materna.dmn.tester.servlets.playground;

import de.materna.dmn.tester.persistence.PersistenceDirectoryManager;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.filters.WriteAccess;
import de.materna.dmn.tester.servlets.playground.beans.Playground;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.serialization.SerializationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Path("/workspaces/{workspace}/playgrounds")
public class PlaygroundServlet {
	private static final Logger log = LoggerFactory.getLogger(PlaygroundServlet.class);

	@GET
	@ReadAccess
	@Produces("application/json")
	public Response getPlaygrounds(@PathParam("workspace") String workspaceUUID, @QueryParam("order") boolean order) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		Map<String, Playground> unsortedPlaygrounds = workspace.getPlaygroundManager().getFiles();
		Map<String, Playground> sortedPlaygrounds = new LinkedHashMap<>();
		unsortedPlaygrounds.entrySet().stream().sorted(Map.Entry.comparingByValue((o1, o2) -> (order ? -1 : 1) * o1.getName().compareTo(o2.getName()))).forEach(entry -> sortedPlaygrounds.put(entry.getKey(), entry.getValue()));

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(sortedPlaygrounds)).build();
	}

	@GET
	@ReadAccess
	@Path("/{uuid}")
	@Produces("application/json")
	public Response getPlayground(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String playgroundUUID) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		if (!workspace.getPlaygroundManager().fileExists(playgroundUUID)) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		Playground playground = workspace.getPlaygroundManager().getFile(playgroundUUID);
		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(playground)).build();
	}

	@POST
	@WriteAccess
	@Consumes("application/json")
	public Response createPlayground(@PathParam("workspace") String workspaceUUID, String body) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		String playgroundUUID = UUID.randomUUID().toString();

		Playground playground = (Playground) SerializationHelper.getInstance().toClass(body, Playground.class);
		if (playground.getName() == null) {
			throw new BadRequestException("Playground name can't be null.");
		}
		workspace.getPlaygroundManager().persistFile(playgroundUUID, playground);

		workspace.getAccessLog().writeMessage("Added playground " + playgroundUUID, System.currentTimeMillis());

		return Response.status(Response.Status.CREATED).entity(SerializationHelper.getInstance().toJSON(playgroundUUID)).build();
	}

	@PUT
	@WriteAccess
	@Path("/{uuid}")
	@Consumes("application/json")
	public Response editPlayground(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String playgroundUUID, String body) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		if (!workspace.getPlaygroundManager().fileExists(playgroundUUID)) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		Playground playground = (Playground) SerializationHelper.getInstance().toClass(body, Playground.class);
		if (playground.getName() == null) {
			throw new BadRequestException("Playground name can't be null.");
		}
		workspace.getPlaygroundManager().persistFile(playgroundUUID, playground);

		workspace.getAccessLog().writeMessage("Edited playground " + playgroundUUID, System.currentTimeMillis());

		return Response.status(Response.Status.OK).build();
	}

	@DELETE
	@WriteAccess
	@Path("/{uuid}")
	public Response deletePlayground(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String playgroundUUID) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		PersistenceDirectoryManager<Playground> playgroundManager = workspace.getPlaygroundManager();
		if (!playgroundManager.fileExists(playgroundUUID)) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		playgroundManager.removeFile(playgroundUUID);

		workspace.getAccessLog().writeMessage("Deleted playground " + playgroundUUID, System.currentTimeMillis());

		return Response.status(Response.Status.NO_CONTENT).build();
	}
}
