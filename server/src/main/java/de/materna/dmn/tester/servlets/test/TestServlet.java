package de.materna.dmn.tester.servlets.test;

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

@Path("/workspaces/{workspace}/tests")
public class TestServlet {

	@GET
	@ReadAccess
	@Produces("application/json")
	public Response getTests(@PathParam("workspace") String workspaceUUID, @QueryParam("order") boolean order)
			throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		Map<String, PersistedTest> unsortedTests = workspace.getTestManager().getFiles();

		Map<String, PersistedTest> sortedTests = new LinkedHashMap<>();
		unsortedTests.entrySet().stream()
				.sorted(Map.Entry.comparingByValue((o1, o2) -> (order ? -1 : 1) * o1.getName().compareTo(o2.getName())))
				.forEach(entry -> sortedTests.put(entry.getKey(), entry.getValue()));

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(sortedTests))
				.build();
	}

	@GET
	@ReadAccess
	@Path("/{uuid}")
	@Produces("application/json")
	public Response getTest(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String testUUID)
			throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		PersistenceDirectoryManager<PersistedTest> testManager = workspace.getTestManager();

		PersistedTest test = testManager.getFile(testUUID);
		if (test == null) {
			throw new NotFoundException();
		}

		workspace.getAccessLog().writeMessage("Accessed test " + testUUID, System.currentTimeMillis());

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(test)).build();
	}

	@POST
	@WriteAccess
	@Consumes("application/json")
	public Response createTest(@PathParam("workspace") String workspaceUUID, String body) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		String uuid = UUID.randomUUID().toString();

		PersistedTest persistedTest = (PersistedTest) SerializationHelper.getInstance().toClass(body,
				PersistedTest.class);
		if (persistedTest.getName() == null) {
			throw new BadRequestException("Test name can't be null.");
		}
		workspace.getTestManager().persistFile(uuid, persistedTest);

		workspace.getAccessLog().writeMessage("Created test " + uuid, System.currentTimeMillis());

		return Response.status(Response.Status.CREATED).entity(uuid).build();
	}

	@POST
	@WriteAccess
	@Path("/{uuid}")
	@Produces("application/json")
	public Response runTest(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String testUUID)
			throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		PersistenceDirectoryManager<PersistedInput> inputManager = workspace.getInputManager();

		PersistedTest test = workspace.getTestManager().getFile(testUUID);
		if (test == null) {
			throw new NotFoundException();
		}

		Map<String, PersistedOutput> expectedOutputs = workspace.getOutputManager().getFiles();

		ExecutionResult executionResult = workspace.getDecisionSession().executeModel(
				DroolsHelper.getMainModelNamespace(workspace),
				InputServlet.enrichInput(inputManager, inputManager.getFile(test.getInput())).getValue());
		Map<String, Object> calculatedOutputs = executionResult.getOutputs();

		Map<String, TestResultOutput> comparedOutputs = new LinkedHashMap<>();
		for (String outputUUID : test.getOutputs()) {
			PersistedOutput expectedOutput = expectedOutputs.get(outputUUID);
			JsonNode calculatedOutputValue = SerializationHelper.getInstance().getJSONMapper()
					.valueToTree(calculatedOutputs.get(expectedOutput.getDecision()));

			comparedOutputs.put(expectedOutput.getDecision(), new TestResultOutput(outputUUID, expectedOutput.getName(),
					expectedOutput.getDecision(), expectedOutput.getValue(), calculatedOutputValue));
		}
		return Response.status(Response.Status.OK).entity(new TestResult(comparedOutputs)).build();
	}

	@PUT
	@WriteAccess
	@Path("/{uuid}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response editTest(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String testUUID,
			String body) throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		PersistenceDirectoryManager<PersistedTest> testManager = workspace.getTestManager();
		if (!testManager.getFiles().containsKey(testUUID)) {
			throw new NotFoundException();
		}

		PersistedTest persistedTest = (PersistedTest) SerializationHelper.getInstance().toClass(body,
				PersistedTest.class);
		if (persistedTest.getName() == null) {
			throw new BadRequestException("Test name can't be null.");
		}
		testManager.persistFile(testUUID, persistedTest);

		workspace.getAccessLog().writeMessage("Edited test " + testUUID, System.currentTimeMillis());

		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@DELETE
	@WriteAccess
	@Path("/{uuid}")
	public Response deleteTest(@PathParam("workspace") String workspaceUUID, @PathParam("uuid") String testUUID)
			throws IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		PersistenceDirectoryManager<PersistedTest> testManager = workspace.getTestManager();

		if (!testManager.getFiles().containsKey(testUUID)) {
			throw new NotFoundException();
		}

		testManager.removeFile(testUUID);

		workspace.getAccessLog().writeMessage("Deleted test " + testUUID, System.currentTimeMillis());

		return Response.status(Response.Status.NO_CONTENT).build();
	}
}
