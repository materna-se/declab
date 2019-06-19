package de.materna.dmn.tester.servlets.input;

import de.materna.dmn.tester.helpers.MergingHelper;
import de.materna.dmn.tester.helpers.SerializationHelper;
import de.materna.dmn.tester.persistence.PersistenceDirectoryManager;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.input.beans.PersistedInput;
import de.materna.dmn.tester.servlets.model.beans.Workspace;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Path("/workspaces/{workspace}")
public class InputServlet {
	private static final Logger log = Logger.getLogger(InputServlet.class);

	@GET
	@Path("/inputs")
	@Produces("application/json")
	public Response getInputs(@PathParam("workspace") String workspaceName, @QueryParam("merge") boolean merge) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceName);
			PersistenceDirectoryManager<PersistedInput> inputManager = workspace.getInputManager();
			Map<String, PersistedInput> workspaceInputs = inputManager.getFiles();

			Map<String, PersistedInput> enrichedInputs = merge ? enrichInputMap(inputManager, workspaceInputs) : workspaceInputs;
			return Response.status(Response.Status.OK).entity(enrichedInputs).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@GET
	@Path("/inputs/{uuid}")
	@Produces("application/json")
	public Response getInput(@PathParam("workspace") String workspaceName, @PathParam("uuid") String inputUUID, @QueryParam("merge") boolean merge) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceName);
			PersistenceDirectoryManager<PersistedInput> inputManager = workspace.getInputManager();

			PersistedInput input = inputManager.getFiles().get(inputUUID);
			if (input == null) {
				throw new NotFoundException();
			}

			PersistedInput enrichedInput = merge ? enrichInput(inputManager, input) : input;
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(enrichedInput)).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@POST
	@Path("/inputs")
	@Consumes("application/json")
	public Response createInput(@PathParam("workspace") String workspaceName, String body) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceName);
			String uuid = UUID.randomUUID().toString();

			workspace.getInputManager().persistFile(uuid, (PersistedInput) SerializationHelper.getInstance().toClass(body, PersistedInput.class));

			return Response.status(Response.Status.CREATED).entity(uuid).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@PUT
	@Path("/inputs/{uuid}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response editInput(@PathParam("workspace") String workspaceName, @PathParam("uuid") String inputUUID, String body) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceName);
			PersistenceDirectoryManager<PersistedInput> inputManager = workspace.getInputManager();
			if (!inputManager.getFiles().containsKey(inputUUID)) {
				throw new NotFoundException();
			}

			PersistedInput input = (PersistedInput) SerializationHelper.getInstance().toClass(body, PersistedInput.class);
			if (inputUUID.equals(input.getParent())) {
				throw new BadRequestException(String.format("Input with uuid %s can't reference itself.", inputUUID));
			}
			inputManager.persistFile(inputUUID, input);

			return Response.status(Response.Status.NO_CONTENT).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@DELETE
	@Path("/inputs/{uuid}")
	public Response deleteInput(@PathParam("workspace") String workspaceName, @PathParam("uuid") String inputUUID) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceName);
			PersistenceDirectoryManager<PersistedInput> inputManager = workspace.getInputManager();

			PersistedInput input = inputManager.getFiles().get(inputUUID);
			if (input == null) {
				throw new NotFoundException(String.format("Can't find input with uuid %s.", inputUUID));
			}
			if (isInputInherited(inputManager, inputUUID)) {
				throw new BadRequestException(String.format("Input with uuid %s is inherited and can't be deleted.", inputUUID));
			}

			inputManager.removeFile(inputUUID);

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
	 * Resolves the entire inheritance of an input to return a deep merged result.
	 */
	public static PersistedInput enrichInput(PersistenceDirectoryManager<PersistedInput> inputManager, PersistedInput input) throws IOException {
		if (input.getParent() == null) {
			return input;
		}

		PersistedInput parentInput = enrichInput(inputManager, inputManager.getFiles().get(input.getParent()));

		return new PersistedInput(input.getName(), input.getParent(), (Map<String, ?>) MergingHelper.merge(parentInput.getValue(), input.getValue()));
	}

	/**
	 * Resolves the entire inheritance of multiple inputs to return a deep merged result.
	 */
	public static Map<String, PersistedInput> enrichInputMap(PersistenceDirectoryManager<PersistedInput> inputManager, Map<String, PersistedInput> input) throws IOException {
		Map<String, PersistedInput> enrichedInput = new HashMap<>();

		for (Map.Entry<String, PersistedInput> entry : input.entrySet()) {
			enrichedInput.put(entry.getKey(), enrichInput(inputManager, entry.getValue()));
		}

		return enrichedInput;
	}
}