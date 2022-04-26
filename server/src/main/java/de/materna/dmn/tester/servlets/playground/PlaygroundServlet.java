package de.materna.dmn.tester.servlets.playground;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import de.materna.dmn.tester.persistence.PersistenceDirectoryManager;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.filters.WriteAccess;
import de.materna.dmn.tester.servlets.playground.beans.Playground;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.serialization.SerializationHelper;

@Path("/workspaces/{workspace}/playgrounds")
public class PlaygroundServlet {

	@GET
	@ReadAccess
	@Produces("application/json")
	public Response getPlaygrounds(@PathParam("workspace") String workspaceUUID, @QueryParam("order") boolean order)
			throws IOException {
		final Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		final Map<String, Playground> unsortedPlaygrounds = workspace.getPlaygroundManager().getFiles();
		final Map<String, Playground> sortedPlaygrounds = new LinkedHashMap<>();
		unsortedPlaygrounds.entrySet().stream()
				.sorted(Map.Entry.comparingByValue((o1, o2) -> (order ? -1 : 1) * o1.getName().compareTo(o2.getName())))
				.forEach(entry -> sortedPlaygrounds.put(entry.getKey(), entry.getValue()));

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(sortedPlaygrounds))
				.build();
	}

	@GET
	@ReadAccess
	@Path("/{uuid}")
	@Produces("application/json")
	public Response getPlayground(@PathParam("workspace") String workspaceUUID,
			@PathParam("uuid") String playgroundUUID) throws IOException {
		final Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		if (!workspace.getPlaygroundManager().fileExists(playgroundUUID)) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		final Playground playground = workspace.getPlaygroundManager().getFile(playgroundUUID);
		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(playground)).build();
	}

	@POST
	@WriteAccess
	@Consumes("application/json")
	public Response createPlayground(@PathParam("workspace") String workspaceUUID, String body) throws IOException {
		final Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		final String playgroundUUID = UUID.randomUUID().toString();

		final Playground playground = (Playground) SerializationHelper.getInstance().toClass(body, Playground.class);
		if (playground.getName() == null) {
			throw new BadRequestException("Playground name can't be null.");
		}
		workspace.getPlaygroundManager().persistFile(playgroundUUID, playground);

		workspace.getAccessLog().writeMessage("Added playground " + playgroundUUID, System.currentTimeMillis());

		return Response.status(Response.Status.CREATED).entity(SerializationHelper.getInstance().toJSON(playgroundUUID))
				.build();
	}

	@PUT
	@WriteAccess
	@Path("/{uuid}")
	@Consumes("application/json")
	public Response editPlayground(@PathParam("workspace") String workspaceUUID,
			@PathParam("uuid") String playgroundUUID, String body) throws IOException {
		final Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		if (!workspace.getPlaygroundManager().fileExists(playgroundUUID)) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		final Playground playground = (Playground) SerializationHelper.getInstance().toClass(body, Playground.class);
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
	public Response deletePlayground(@PathParam("workspace") String workspaceUUID,
			@PathParam("uuid") String playgroundUUID) throws IOException {
		final Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		final PersistenceDirectoryManager<Playground> playgroundManager = workspace.getPlaygroundManager();
		if (!playgroundManager.fileExists(playgroundUUID)) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		playgroundManager.removeFile(playgroundUUID);

		workspace.getAccessLog().writeMessage("Deleted playground " + playgroundUUID, System.currentTimeMillis());

		return Response.status(Response.Status.NO_CONTENT).build();
	}
}
