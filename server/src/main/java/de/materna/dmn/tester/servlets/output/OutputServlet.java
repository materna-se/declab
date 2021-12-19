package de.materna.dmn.tester.servlets.output;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.BadRequestException;
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
import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.filters.WriteAccess;
import de.materna.dmn.tester.servlets.output.beans.PersistedOutput;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.serialization.SerializationHelper;

@Path("/workspaces/{workspace}/outputs")
public class OutputServlet {

	@GET
	@ReadAccess
	@Produces("application/json")
	public Response getOutputs(@PathParam("workspace") String workspaceUUID, @QueryParam("order") boolean order)
			throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		Map<String, PersistedOutput> unsortedOutputs = workspace.getOutputManager().getFiles();

		Map<String, PersistedOutput> sortedOutputs = new LinkedHashMap<>();
		unsortedOutputs.entrySet().stream()
				.sorted(Map.Entry.comparingByValue((o1, o2) -> (order ? -1 : 1) * o1.getName().compareTo(o2.getName())))
				.forEach(entry -> sortedOutputs.put(entry.getKey(), entry.getValue()));

		return Response.status(Response.Status.OK).entity(sortedOutputs).build();
	}

	@GET
	@ReadAccess
	@Path("/{uuid}")
	@Produces("application/json")
	public Response getOutput(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String outputUUID)
			throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		PersistenceDirectoryManager<PersistedOutput> outputManager = workspace.getOutputManager();

		PersistedOutput output = outputManager.getFile(outputUUID);
		if (output == null) {
			throw new NotFoundException();
		}

		workspace.getAccessLog().writeMessage("Accessed output " + outputUUID, System.currentTimeMillis());

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(output)).build();
	}

	@POST
	@WriteAccess
	@Consumes("application/json")
	public Response createOutput(@PathParam("workspace") String workspaceUUID, String body) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		String uuid = UUID.randomUUID().toString();

		PersistedOutput persistedOutput = (PersistedOutput) SerializationHelper.getInstance().toClass(body,
				PersistedOutput.class);
		if (persistedOutput.getName() == null) {
			throw new BadRequestException("Output name can't be null.");
		}
		workspace.getOutputManager().persistFile(uuid, persistedOutput);

		workspace.getAccessLog().writeMessage("Created output " + uuid, System.currentTimeMillis());

		return Response.status(Response.Status.CREATED).entity(uuid).build();
	}

	@PUT
	@WriteAccess
	@Path("/{uuid}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response editOutput(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String outputUUID,
			String body) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		PersistenceDirectoryManager<PersistedOutput> outputManager = workspace.getOutputManager();
		if (!outputManager.getFiles().containsKey(outputUUID)) {
			throw new NotFoundException();
		}

		PersistedOutput persistedOutput = (PersistedOutput) SerializationHelper.getInstance().toClass(body,
				PersistedOutput.class);
		if (persistedOutput.getName() == null) {
			throw new BadRequestException("Output name can't be null.");
		}
		outputManager.persistFile(outputUUID, persistedOutput);

		workspace.getAccessLog().writeMessage("Edited output " + outputUUID, System.currentTimeMillis());

		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@DELETE
	@WriteAccess
	@Path("/{uuid}")
	public Response deleteOutput(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String outputUUID)
			throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		PersistenceDirectoryManager<PersistedOutput> outputManager = workspace.getOutputManager();
		if (!outputManager.getFiles().containsKey(outputUUID)) {
			throw new NotFoundException();
		}

		outputManager.removeFile(outputUUID);

		workspace.getAccessLog().writeMessage("Deleted output " + outputUUID, System.currentTimeMillis());

		return Response.status(Response.Status.NO_CONTENT).build();
	}
}
