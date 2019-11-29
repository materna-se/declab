package de.materna.dmn.tester.servlets.test;

import de.materna.dmn.tester.drools.DroolsExecutor;
import de.materna.dmn.tester.persistence.PersistenceDirectoryManager;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.input.InputServlet;
import de.materna.dmn.tester.servlets.input.beans.PersistedInput;
import de.materna.dmn.tester.servlets.output.beans.EnrichedOutput;
import de.materna.dmn.tester.servlets.output.beans.Output;
import de.materna.dmn.tester.servlets.output.beans.PersistedOutput;
import de.materna.dmn.tester.servlets.test.beans.PersistedTest;
import de.materna.dmn.tester.servlets.test.beans.TestResult;
import de.materna.dmn.tester.servlets.test.beans.TestResultOutput;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.serialization.SerializationHelper;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Path("/workspaces/{workspace}")
public class TestServlet {
	private static final Logger log = Logger.getLogger(TestServlet.class);

	@GET
	@Path("/tests")
	@Produces("application/json")
	public Response getTests(@PathParam("workspace") String workspaceName) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceName);
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(workspace.getTestManager().getFiles())).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@GET
	@Path("/tests/{uuid}")
	@Produces("application/json")
	public Response getTest(@PathParam("workspace") String workspaceName, @PathParam("uuid") String testUUID) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceName);
			PersistenceDirectoryManager<PersistedTest> testManager = workspace.getTestManager();

			PersistedTest test = testManager.getFiles().get(testUUID);
			if (test == null) {
				throw new NotFoundException();
			}

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(test)).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@POST
	@Path("/tests")
	@Consumes("application/json")
	public Response createTest(@PathParam("workspace") String workspaceName, String body) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceName);
			String uuid = UUID.randomUUID().toString();

			workspace.getTestManager().persistFile(uuid, (PersistedTest) SerializationHelper.getInstance().toClass(body, PersistedTest.class));

			return Response.status(Response.Status.CREATED).entity(uuid).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@POST
	@Path("/tests/{uuid}")
	@Produces("application/json")
	public Response runTest(@PathParam("workspace") String workspaceName, @PathParam("uuid") String testUUID) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceName);
			PersistenceDirectoryManager<PersistedInput> inputManager = workspace.getInputManager();
			PersistenceDirectoryManager<PersistedTest> testManager = workspace.getTestManager();

			PersistedTest test = testManager.getFiles().get(testUUID);
			if (test == null) {
				throw new NotFoundException();
			}

			Map<String, PersistedOutput> expectedOutputs = workspace.getOutputManager().getFiles();
			// drools will execute the persisted input, the result is in the format <Decision, Output>.
			Map<String, Output> calculatedOutputs = DroolsExecutor.getOutputs(workspace.getDecisionSession(), InputServlet.enrichInput(inputManager, inputManager.getFiles().get(test.getInput())).getValue());

			Map<String, TestResultOutput> comparedOutputs = new HashMap<>();

			for (String output : test.getOutputs()) {
				PersistedOutput expectedOutput = expectedOutputs.get(output);
				Output calculatedOutput = calculatedOutputs.get(expectedOutput.getDecision());

				comparedOutputs.put(expectedOutput.getDecision(), new TestResultOutput(new EnrichedOutput(output, expectedOutput), calculatedOutput));
			}

			return Response.status(Response.Status.OK).entity(new TestResult(comparedOutputs)).build();
		}
		catch (IOException | DatatypeConfigurationException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@PUT
	@Path("/tests/{uuid}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response editTest(@PathParam("workspace") String workspaceName, @PathParam("uuid") String testUUID, String body) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceName);
			PersistenceDirectoryManager<PersistedTest> testManager = workspace.getTestManager();

			if (!testManager.getFiles().containsKey(testUUID)) {
				throw new NotFoundException();
			}

			testManager.persistFile(testUUID, (PersistedTest) SerializationHelper.getInstance().toClass(body, PersistedTest.class));

			return Response.status(Response.Status.NO_CONTENT).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@DELETE
	@Path("/tests/{uuid}")
	public Response deleteTest(@PathParam("workspace") String workspaceName, @PathParam("uuid") String testUUID) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceName);
			PersistenceDirectoryManager<PersistedTest> testManager = workspace.getTestManager();

			if (!testManager.getFiles().containsKey(testUUID)) {
				throw new NotFoundException();
			}

			testManager.removeFile(testUUID);

			return Response.status(Response.Status.NO_CONTENT).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}
}