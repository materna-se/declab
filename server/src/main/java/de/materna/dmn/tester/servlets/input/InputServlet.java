package de.materna.dmn.tester.servlets.input;

import de.materna.dmn.tester.helpers.MergingHelper;
import de.materna.dmn.tester.persistence.PersistenceDirectoryManager;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.filters.WriteAccess;
import de.materna.dmn.tester.servlets.input.beans.PersistedInput;
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
public class InputServlet {
	private static final Logger log = Logger.getLogger(InputServlet.class);

	@GET
	@ReadAccess
	@Path("/inputs")
	@Produces("application/json")
	public Response getInputs(@PathParam("workspace") String workspaceUUID, @QueryParam("merge") boolean merge, @QueryParam("order") boolean order) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
			PersistenceDirectoryManager<PersistedInput> inputManager = workspace.getInputManager();

			Map<String, PersistedInput> unsortedInputs = merge ? enrichInputMap(inputManager, inputManager.getFiles()) : inputManager.getFiles();

			Map<String, PersistedInput> sortedInputs = new LinkedHashMap<>();
			unsortedInputs.entrySet().stream().sorted(Map.Entry.comparingByValue((o1, o2) -> (order ? -1 : 1) * o1.getName().compareTo(o2.getName()))).forEach(entry -> sortedInputs.put(entry.getKey(), entry.getValue()));

			return Response.status(Response.Status.OK).entity(sortedInputs).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@GET
	@ReadAccess
	@Path("/inputs/{uuid}")
	@Produces("application/json")
	public Response getInput(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String inputUUID, @QueryParam("merge") boolean merge) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
			PersistenceDirectoryManager<PersistedInput> inputManager = workspace.getInputManager();

			PersistedInput input = inputManager.getFile(inputUUID);
			if (input == null) {
				throw new NotFoundException();
			}

			PersistedInput enrichedInput = merge ? enrichInput(inputManager, input) : input;

			workspace.getAccessLog().writeMessage("Accessed input " + inputUUID, System.currentTimeMillis());

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(enrichedInput)).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@POST
	@WriteAccess
	@Path("/inputs")
	@Consumes("application/json")
	public Response createInput(@PathParam("workspace") String workspaceUUID, String body) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
			String uuid = UUID.randomUUID().toString();

			workspace.getInputManager().persistFile(uuid, (PersistedInput) SerializationHelper.getInstance().toClass(body, PersistedInput.class));

			workspace.getAccessLog().writeMessage("Created input" + uuid, System.currentTimeMillis());

			return Response.status(Response.Status.CREATED).entity(uuid).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@PUT
	@WriteAccess
	@Path("/inputs/{uuid}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response editInput(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String inputUUID, String body) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
			PersistenceDirectoryManager<PersistedInput> inputManager = workspace.getInputManager();
			if (!inputManager.getFiles().containsKey(inputUUID)) {
				throw new NotFoundException();
			}

			PersistedInput input = (PersistedInput) SerializationHelper.getInstance().toClass(body, PersistedInput.class);
			if (inputUUID.equals(input.getParent())) {
				throw new BadRequestException(String.format("Input with uuid %s can't reference itself.", inputUUID));
			}
			inputManager.persistFile(inputUUID, input);

			workspace.getAccessLog().writeMessage("Edited input " + inputUUID, System.currentTimeMillis());

			return Response.status(Response.Status.NO_CONTENT).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@DELETE
	@WriteAccess
	@Path("/inputs/{uuid}")
	public Response deleteInput(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String inputUUID) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
			PersistenceDirectoryManager<PersistedInput> inputManager = workspace.getInputManager();

			PersistedInput input = inputManager.getFile(inputUUID);
			if (input == null) {
				throw new NotFoundException(String.format("Can't find input with uuid %s.", inputUUID));
			}
			if (isInputInherited(inputManager, inputUUID)) {
				throw new BadRequestException(String.format("Input with uuid %s is inherited and can't be deleted.", inputUUID));
			}

			inputManager.removeFile(inputUUID);

			workspace.getAccessLog().writeMessage("Deleted input " + inputUUID, System.currentTimeMillis());

			return Response.status(Response.Status.NO_CONTENT).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	private boolean isInputInherited(PersistenceDirectoryManager<PersistedInput> inputManager, String inputUUID) throws IOException {
		for (PersistedInput input : inputManager.getFiles().values()) {
			if (inputUUID.equals(input.getParent())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Resolves the entire inheritance of multiple inputs to return a deep merged result.
	 */
	public static Map<String, PersistedInput> enrichInputMap(PersistenceDirectoryManager<PersistedInput> inputManager, Map<String, PersistedInput> input) throws IOException {
		Map<String, PersistedInput> enrichedInput = new LinkedHashMap<>();

		for (Map.Entry<String, PersistedInput> entry : input.entrySet()) {
			enrichedInput.put(entry.getKey(), enrichInput(inputManager, entry.getValue()));
		}

		return enrichedInput;
	}

	/**
	 * Resolves the entire inheritance of an input to return a deep merged result.
	 */
	public static PersistedInput enrichInput(PersistenceDirectoryManager<PersistedInput> inputManager, PersistedInput input) throws IOException {
		if (input.getParent() == null) {
			return input;
		}

		PersistedInput parentInput = enrichInput(inputManager, inputManager.getFile(input.getParent()));

		return new PersistedInput(input.getName(), input.getParent(), (Map<String, ?>) MergingHelper.merge(parentInput.getValue(), input.getValue()));
	}
}