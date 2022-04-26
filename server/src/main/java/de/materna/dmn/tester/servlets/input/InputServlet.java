package de.materna.dmn.tester.servlets.input;

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

import de.materna.dmn.tester.helpers.MergingHelper;
import de.materna.dmn.tester.persistence.PersistenceDirectoryManager;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.filters.WriteAccess;
import de.materna.dmn.tester.servlets.input.beans.PersistedInput;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.serialization.SerializationHelper;

@Path("/workspaces/{workspace}/inputs")
public class InputServlet {

	@GET
	@ReadAccess
	@Produces("application/json")
	public Response getInputs(@PathParam("workspace") String workspaceUUID, @QueryParam("merge") boolean merge,
			@QueryParam("order") boolean order) throws IOException {
		final Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		final PersistenceDirectoryManager<PersistedInput> inputManager = workspace.getInputManager();

		final Map<String, PersistedInput> unsortedInputs = merge ? enrichInputMap(inputManager, inputManager.getFiles())
				: inputManager.getFiles();

		final Map<String, PersistedInput> sortedInputs = new LinkedHashMap<>();
		unsortedInputs.entrySet().stream()
				.sorted(Map.Entry.comparingByValue((o1, o2) -> (order ? -1 : 1) * o1.getName().compareTo(o2.getName())))
				.forEach(entry -> sortedInputs.put(entry.getKey(), entry.getValue()));

		return Response.status(Response.Status.OK).entity(sortedInputs).build();
	}

	@GET
	@ReadAccess
	@Path("/{uuid}")
	@Produces("application/json")
	public Response getInput(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String inputUUID,
			@QueryParam("merge") boolean merge) throws IOException {
		final Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		final PersistenceDirectoryManager<PersistedInput> inputManager = workspace.getInputManager();

		final PersistedInput input = inputManager.getFile(inputUUID);
		if (input == null) {
			throw new NotFoundException();
		}

		final PersistedInput enrichedInput = merge ? enrichInput(inputManager, input) : input;

		workspace.getAccessLog().writeMessage("Accessed input " + inputUUID, System.currentTimeMillis());

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(enrichedInput))
				.build();
	}

	@POST
	@WriteAccess
	@Consumes("application/json")
	public Response createInput(@PathParam("workspace") String workspaceUUID, String body)
			throws IOException, RuntimeException {
		final Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		final String uuid = UUID.randomUUID().toString();

		final PersistedInput persistedInput = (PersistedInput) SerializationHelper.getInstance().toClass(body,
				PersistedInput.class);
		if (persistedInput.getName() == null) {
			throw new BadRequestException("Input name can't be null.");
		}
		workspace.getInputManager().persistFile(uuid, persistedInput);

		workspace.getAccessLog().writeMessage("Created input" + uuid, System.currentTimeMillis());

		return Response.status(Response.Status.CREATED).entity(uuid).build();
	}

	@PUT
	@WriteAccess
	@Path("/{uuid}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response editInput(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String inputUUID,
			String body) throws IOException {
		final Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		final PersistenceDirectoryManager<PersistedInput> inputManager = workspace.getInputManager();
		if (!inputManager.getFiles().containsKey(inputUUID)) {
			throw new NotFoundException();
		}

		final PersistedInput persistedInput = (PersistedInput) SerializationHelper.getInstance().toClass(body,
				PersistedInput.class);
		if (persistedInput.getName() == null) {
			throw new BadRequestException("Input name can't be null.");
		}
		if (inputUUID.equals(persistedInput.getParent())) {
			throw new BadRequestException(String.format("Input with uuid %s can't reference itself.", inputUUID));
		}
		inputManager.persistFile(inputUUID, persistedInput);

		workspace.getAccessLog().writeMessage("Edited input " + inputUUID, System.currentTimeMillis());

		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@DELETE
	@WriteAccess
	@Path("/{uuid}")
	public Response deleteInput(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String inputUUID)
			throws IOException {
		final Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		final PersistenceDirectoryManager<PersistedInput> inputManager = workspace.getInputManager();

		final PersistedInput input = inputManager.getFile(inputUUID);
		if (input == null) {
			throw new NotFoundException(String.format("Can't find input with uuid %s.", inputUUID));
		}
		if (isInputInherited(inputManager, inputUUID)) {
			throw new BadRequestException(
					String.format("Input with uuid %s is inherited and can't be deleted.", inputUUID));
		}

		inputManager.removeFile(inputUUID);

		workspace.getAccessLog().writeMessage("Deleted input " + inputUUID, System.currentTimeMillis());

		return Response.status(Response.Status.NO_CONTENT).build();
	}

	private boolean isInputInherited(PersistenceDirectoryManager<PersistedInput> inputManager, String inputUUID)
			throws IOException {
		for (final PersistedInput input : inputManager.getFiles().values()) {
			if (inputUUID.equals(input.getParent())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Resolves the entire inheritance of multiple inputs to return a deep merged
	 * result.
	 */
	public static Map<String, PersistedInput> enrichInputMap(PersistenceDirectoryManager<PersistedInput> inputManager,
			Map<String, PersistedInput> input) throws IOException {
		final Map<String, PersistedInput> enrichedInput = new LinkedHashMap<>();

		for (final Map.Entry<String, PersistedInput> entry : input.entrySet()) {
			enrichedInput.put(entry.getKey(), enrichInput(inputManager, entry.getValue()));
		}

		return enrichedInput;
	}

	/**
	 * Resolves the entire inheritance of an input to return a deep merged result.
	 */
	@SuppressWarnings("unchecked")
	public static PersistedInput enrichInput(PersistenceDirectoryManager<PersistedInput> inputManager,
			PersistedInput input) throws IOException {
		if (input.getParent() == null) {
			return input;
		}

		final PersistedInput parentInput = enrichInput(inputManager, inputManager.getFile(input.getParent()));

		return new PersistedInput(input.getName(), input.getParent(),
				(Map<String, Object>) MergingHelper.merge(parentInput.getValue(), input.getValue()));
	}
}