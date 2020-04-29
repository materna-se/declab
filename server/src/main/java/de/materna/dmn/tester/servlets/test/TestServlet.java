package de.materna.dmn.tester.servlets.test;

import com.fasterxml.jackson.databind.JsonNode;
import de.materna.dmn.tester.drools.helpers.DroolsHelper;
import de.materna.dmn.tester.persistence.PersistenceDirectoryManager;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.filters.WriteAccess;
import de.materna.dmn.tester.servlets.input.InputServlet;
import de.materna.dmn.tester.servlets.input.beans.PersistedInput;
import de.materna.dmn.tester.servlets.output.beans.PersistedOutput;
import de.materna.dmn.tester.servlets.test.beans.PersistedTest;
import de.materna.dmn.tester.servlets.test.beans.TestResult;
import de.materna.dmn.tester.servlets.test.beans.TestResultOutput;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.model.ExecutionResult;
import de.materna.jdec.serialization.SerializationHelper;
import org.apache.log4j.Logger;
import org.kie.dmn.api.core.DMNModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Path("/workspaces/{workspace}")
public class TestServlet {
	private static final Logger log = Logger.getLogger(TestServlet.class);

	@GET
	@ReadAccess
	@Path("/tests")
	@Produces("application/json")
	public Response getTests(@PathParam("workspace") String workspaceUUID, @QueryParam("order") boolean order) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

			Map<String, PersistedTest> unsortedTests = workspace.getTestManager().getFiles();

			Map<String, PersistedTest> sortedTests = new LinkedHashMap<>();
			unsortedTests.entrySet().stream().sorted(Map.Entry.comparingByValue((o1, o2) -> (order ? -1 : 1) * o1.getName().compareTo(o2.getName()))).forEach(entry -> sortedTests.put(entry.getKey(), entry.getValue()));

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(sortedTests)).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@GET
	@ReadAccess
	@Path("/tests/{uuid}")
	@Produces("application/json")
	public Response getTest(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String testUUID) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
			PersistenceDirectoryManager<PersistedTest> testManager = workspace.getTestManager();

			PersistedTest test = testManager.getFile(testUUID);
			if (test == null) {
				throw new NotFoundException();
			}

			workspace.getAccessLog().writeMessage("Accessed test " + testUUID, System.currentTimeMillis());

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(test)).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@POST
	@WriteAccess
	@Path("/tests")
	@Consumes("application/json")
	public Response createTest(@PathParam("workspace") String workspaceUUID, String body) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
			String uuid = UUID.randomUUID().toString();

			workspace.getTestManager().persistFile(uuid, (PersistedTest) SerializationHelper.getInstance().toClass(body, PersistedTest.class));

			workspace.getAccessLog().writeMessage("Created test " + uuid, System.currentTimeMillis());

			return Response.status(Response.Status.CREATED).entity(uuid).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@POST
	@WriteAccess
	@Path("/tests/{uuid}")
	@Produces("application/json")
	public Response runTest(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String testUUID) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
			PersistenceDirectoryManager<PersistedInput> inputManager = workspace.getInputManager();
			PersistenceDirectoryManager<PersistedTest> testManager = workspace.getTestManager();

			PersistedTest test = testManager.getFile(testUUID);
			if (test == null) {
				throw new NotFoundException();
			}

			Map<String, PersistedOutput> expectedOutputs = workspace.getOutputManager().getFiles();

			ExecutionResult executionResult = workspace.getDecisionSession().executeModel(DroolsHelper.getModel(workspace), InputServlet.enrichInput(inputManager, inputManager.getFile(test.getInput())).getValue());
			Map<String, Object> calculatedOutputs = executionResult.getOutputs();

			Map<String, TestResultOutput> comparedOutputs = new LinkedHashMap<>();
			for (String output : test.getOutputs()) {
				PersistedOutput expectedOutput = expectedOutputs.get(output);
				JsonNode calculatedOutputValue = SerializationHelper.getInstance().getJSONMapper().valueToTree(calculatedOutputs.get(expectedOutput.getDecision()));

				comparedOutputs.put(expectedOutput.getDecision(), new TestResultOutput(expectedOutput.getValue(), calculatedOutputValue));
			}
			return Response.status(Response.Status.OK).entity(new TestResult(comparedOutputs)).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@PUT
	@WriteAccess
	@Path("/tests/{uuid}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response editTest(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String testUUID, String body) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
			PersistenceDirectoryManager<PersistedTest> testManager = workspace.getTestManager();

			if (!testManager.getFiles().containsKey(testUUID)) {
				throw new NotFoundException();
			}

			testManager.persistFile(testUUID, (PersistedTest) SerializationHelper.getInstance().toClass(body, PersistedTest.class));

			workspace.getAccessLog().writeMessage("Edited test " + testUUID, System.currentTimeMillis());

			return Response.status(Response.Status.NO_CONTENT).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@DELETE
	@WriteAccess
	@Path("/tests/{uuid}")
	public Response deleteTest(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String testUUID) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
			PersistenceDirectoryManager<PersistedTest> testManager = workspace.getTestManager();

			if (!testManager.getFiles().containsKey(testUUID)) {
				throw new NotFoundException();
			}

			testManager.removeFile(testUUID);

			workspace.getAccessLog().writeMessage("Deleted test " + testUUID, System.currentTimeMillis());

			return Response.status(Response.Status.NO_CONTENT).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}
}