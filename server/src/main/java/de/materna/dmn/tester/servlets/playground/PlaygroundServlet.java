package de.materna.dmn.tester.servlets.playground;

import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.filters.WriteAccess;
import de.materna.dmn.tester.servlets.playground.beans.Playground;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.serialization.SerializationHelper;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Path("/workspaces/{workspace}")
public class PlaygroundServlet {
	private static final Logger log = Logger.getLogger(PlaygroundServlet.class);

	@GET
	@ReadAccess
	@Path("/playgrounds")
	@Produces("application/json")
	public Response getPlaygrounds(@PathParam("workspace") String workspaceUUID, @QueryParam("order") boolean order) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

			Map<String, Playground> unsortedPlaygrounds = workspace.getPlaygroundManager().getFiles();
			Map<String, Playground> sortedPlaygrounds = new LinkedHashMap<>();
			unsortedPlaygrounds.entrySet().stream().sorted(Map.Entry.comparingByValue((o1, o2) -> (order ? -1 : 1) * o1.getName().compareTo(o2.getName()))).forEach(entry -> sortedPlaygrounds.put(entry.getKey(), entry.getValue()));

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(sortedPlaygrounds)).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@GET
	@ReadAccess
	@Path("/playgrounds/{uuid}")
	@Produces("application/json")
	public Response getPlayground(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String playgroundUUID) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

			Playground playground = workspace.getPlaygroundManager().getFile(playgroundUUID);
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(playground)).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@POST
	@WriteAccess
	@Path("/playgrounds")
	@Consumes("application/json")
	public Response createPlayground(@PathParam("workspace") String workspaceUUID, String body) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

			String playgroundUUID = UUID.randomUUID().toString();

			Playground playground = (Playground) SerializationHelper.getInstance().toClass(body, Playground.class);

			//Validate
			if (playground.name == null || playground.name.length() == 0) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}

			workspace.getPlaygroundManager().persistFile(playgroundUUID, playground);

			workspace.getAccessLog().writeMessage("Added playground " + playgroundUUID, System.currentTimeMillis());

			return Response.status(Response.Status.CREATED).entity(SerializationHelper.getInstance().toJSON(playgroundUUID)).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@PUT
	@WriteAccess
	@Path("/playgrounds/{uuid}")
	@Consumes("application/json")
	public Response editPlayground(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String playgroundUUID, String body) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

			Playground playground = (Playground) SerializationHelper.getInstance().toClass(body, Playground.class);

			if (!workspace.getPlaygroundManager().fileExists(playgroundUUID)) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}

			if (playground.name == null || playground.name.length() == 0) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}

			workspace.getPlaygroundManager().persistFile(playgroundUUID, playground);

			workspace.getAccessLog().writeMessage("Edited playground " + playgroundUUID, System.currentTimeMillis());

			return Response.status(Response.Status.OK).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@DELETE
	@WriteAccess
	@Path("/playgrounds/{uuid}")
	public Response deletePlayground(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String playgroundUUID) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

			if (!workspace.getPlaygroundManager().fileExists(playgroundUUID)) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}

			workspace.getPlaygroundManager().removeFile(playgroundUUID);

			workspace.getAccessLog().writeMessage("Deleted playground " + playgroundUUID, System.currentTimeMillis());

			return Response.status(Response.Status.OK).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}
}
