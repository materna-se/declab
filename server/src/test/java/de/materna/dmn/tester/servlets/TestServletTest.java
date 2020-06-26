package de.materna.dmn.tester.servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import de.materna.dmn.tester.FileHelper;
import de.materna.dmn.tester.ManagementHelper;
import de.materna.dmn.tester.RequestHelper;
import de.materna.dmn.tester.servlets.test.beans.PersistedTest;
import de.materna.dmn.tester.servlets.test.beans.TestResult;
import de.materna.jdec.serialization.SerializationHelper;

public class TestServletTest {
	static String declabHost = ManagementHelper.declabHost;
	static String workspaceUUID = "";

	@BeforeEach
	public void createWorkspace() throws URISyntaxException, IOException {
		String createdWorkspaceUUID = RequestHelper.emitRequest(declabHost + "/api/workspaces", "POST", null, FileHelper.readFile("workspace_public.json"), MediaType.APPLICATION_JSON, 200, true);
		Assertions.assertTrue(createdWorkspaceUUID != null && createdWorkspaceUUID.length() > 0);

		workspaceUUID = createdWorkspaceUUID;
	}

	@Test
	public void verifyResponseStatuses() throws URISyntaxException, IOException {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);

		String urlTemplate;
		String url;

		String testUUID;

		String testValid1Json = FileHelper.readFile("test-test-1", "test_valid_1.json");
		String testValid2Json = FileHelper.readFile("test-test-1", "test_valid_2.json");

		//Check for non-existent tests, make sure all endpoints handle as expected
		{
			String randomUUID = UUID.randomUUID().toString();
			urlTemplate = declabHost + "/api/workspaces/" + randomUUID + "/tests";

			url = urlTemplate;
			RequestHelper.emitRequest(url, "GET", null, 404);
			RequestHelper.emitRequest(url, "POST", null, 415);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 404, false);

			url = urlTemplate + "/" + randomUUID;
			RequestHelper.emitRequest(url, "GET", null, 404);
			RequestHelper.emitRequest(url, "POST", null, 404);
			RequestHelper.emitRequest(url, "PUT", null, 415);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 404, false);
			RequestHelper.emitRequest(url, "DELETE", null, 404);
		}

		//Make workspace public
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("workspace_public.json"), MediaType.APPLICATION_JSON, 200, true);
		}

		//Check all non-test-specific endpoints in public workspace
		{
			urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/tests";

			url = urlTemplate;
			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "POST", null, 415);
			RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 400, false);
		}

		//Try creating invalid tests in public workspace
		{
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("test-test-1", "test_invalid_1.json"), MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("test-test-1", "test_invalid_2.json"), MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("test-test-1", "test_invalid_3.json"), MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("test-test-1", "test_invalid_4.json"), MediaType.APPLICATION_JSON, 400, false);
		}

		//Create valid test in public workspace
		{
			testUUID = RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("test-test-1", "test_valid_1.json"), MediaType.APPLICATION_JSON, 201, true);
		}

		//Check all test-specific endpoints in public workspace
		{
			url += "/" + testUUID;
			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "POST", null, 503);
			RequestHelper.emitRequest(url, "PUT", null, 415);
			RequestHelper.emitRequest(url, "PUT", null, "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "PUT", null, FileHelper.readFile("test-test-1", "test_valid_2.json"), MediaType.APPLICATION_JSON, 204, false);
			RequestHelper.emitRequest(url, "DELETE", null, 204);
		}


		//Make workspace protected
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
			String protectedWorkspaceJson = FileHelper.readFile("workspace_protected.json");
			RequestHelper.emitRequest(url, "POST", null, protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
			RequestHelper.emitRequest(url, "POST", "test", protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		}

		//Check all non-test-specific endpoints in protected workspace
		{
			urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/tests";

			url = urlTemplate;
			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "POST", "test", 415);
			RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		}

		//Create valid test in protected workspace
		{
			RequestHelper.emitRequest(url, "POST", null, testValid1Json, MediaType.APPLICATION_JSON, 401, true);
			testUUID = RequestHelper.emitRequest(url, "POST", "test", testValid1Json, MediaType.APPLICATION_JSON, 201, true);
		}

		//Check all test-specific endpoints in protected workspace
		{
			url += "/" + testUUID;

			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "POST", null, 401);
			RequestHelper.emitRequest(url, "POST", "test", 503);
			RequestHelper.emitRequest(url, "PUT", "test", 415);
			RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "PUT", null, testValid2Json, MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", testValid2Json, MediaType.APPLICATION_JSON, 204, false);
			RequestHelper.emitRequest(url, "DELETE", null, 401);
			RequestHelper.emitRequest(url, "DELETE", "test", 204);
		}


		//Make workspace private
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
			String privateWorkspaceJson = FileHelper.readFile("workspace_private.json");
			RequestHelper.emitRequest(url, "POST", null, privateWorkspaceJson, MediaType.APPLICATION_JSON, 401, true);
			RequestHelper.emitRequest(url, "POST", "test", privateWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		}

		//Check all non-test-specific endpoints in private workspace
		{
			urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/tests";

			url = urlTemplate;
			RequestHelper.emitRequest(url, "GET", null, 401);
			RequestHelper.emitRequest(url, "GET", "test", 200);
			RequestHelper.emitRequest(url, "POST", "test", 415);
			RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		}

		//Create valid test in private workspace
		{
			RequestHelper.emitRequest(url, "POST", null, testValid1Json, MediaType.APPLICATION_JSON, 401, true);
			testUUID = RequestHelper.emitRequest(url, "POST", "test", testValid1Json, MediaType.APPLICATION_JSON, 201, true);
		}

		//Check all test-specific endpoints in private workspace
		{
			url += "/" + testUUID;

			RequestHelper.emitRequest(url, "GET", null, 401);
			RequestHelper.emitRequest(url, "GET", "test", 200);
			RequestHelper.emitRequest(url, "POST", null, 401);
			RequestHelper.emitRequest(url, "POST", "test", 503);
			RequestHelper.emitRequest(url, "PUT", "test", 415);
			RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "PUT", null, testValid2Json, MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", testValid2Json, MediaType.APPLICATION_JSON, 204, false);
			RequestHelper.emitRequest(url, "DELETE", null, 401);
			RequestHelper.emitRequest(url, "DELETE", "test", 204);
		}

		//Make workspace public again to be tidy
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
			String publicWorkspaceJson = FileHelper.readFile("workspace_public.json");
			RequestHelper.emitRequest(url, "POST", null, publicWorkspaceJson, MediaType.APPLICATION_JSON, 401, true);
			RequestHelper.emitRequest(url, "POST", "test", publicWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		}
	}

	@Test
	public void verifyFunctionality() throws URISyntaxException, UnsupportedEncodingException, IOException {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);

		String url;

		String inputUUID, output1UUID, output2UUID;

		PersistedTest test;
		String testUUID;

		//Set up and create test
		{
			//Import models
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/model";
			RequestHelper.emitRequest(url, "PUT", null, FileHelper.readFile("model-test-1", "anbieter_nutzer.json"), MediaType.APPLICATION_JSON, 200, false);

			//Create input in workspace
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs";
			inputUUID = RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("test-test-1", "input_1.json"), MediaType.APPLICATION_JSON,201, true);

			//Create outputs in workspace
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs";
			output1UUID = RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("test-test-1", "output_1.json"), MediaType.APPLICATION_JSON, 201, true);
			output2UUID = RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("test-test-1", "output_2.json"), MediaType.APPLICATION_JSON, 201, true);

			//Keep track of outputs in a list
			List<String> outputs = new LinkedList<String>();
			outputs.add(output1UUID);
			outputs.add(output2UUID);

			//Create test object
			test = new PersistedTest();
			test.setName("test1");
			test.setDescription("description1");
			test.setInput(inputUUID);
			test.setOutputs(outputs);

			//Serialize test object to JSON
			String testJson = SerializationHelper.getInstance().toJSON(test);

			//Create test in workspace
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/tests";
			testUUID = RequestHelper.emitRequest(url, "POST", null, testJson, MediaType.APPLICATION_JSON, 201, true);
		}

		//Get test indirectly, by getting all tests in the workspace
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/tests";

			String testsJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			HashMap<String, PersistedTest> tests = SerializationHelper.getInstance().toClass(testsJson, new TypeReference<HashMap<String, PersistedTest>>() {});

			Assertions.assertEquals(1, tests.size());

			//Compare to original test object
			PersistedTest testToCompare = tests.get(testUUID);
			Assertions.assertEquals(test.getName(), testToCompare.getName());
			Assertions.assertEquals(test.getDescription(), testToCompare.getDescription());
			Assertions.assertEquals(test.getInput(), testToCompare.getInput());
			Assertions.assertEquals(test.getOutputs(), testToCompare.getOutputs());
		}

		//Get test directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID +  "/tests/" + testUUID;

			String testJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			PersistedTest testToCompare = (PersistedTest) SerializationHelper.getInstance().toClass(testJson, PersistedTest.class);

			//Compare to original test object
			Assertions.assertEquals(test.getName(), testToCompare.getName());
			Assertions.assertEquals(test.getDescription(), testToCompare.getDescription());
			Assertions.assertEquals(test.getInput(), testToCompare.getInput());
			Assertions.assertEquals(test.getOutputs(), testToCompare.getOutputs());
		}

		//Run test
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID +  "/tests/" + testUUID;

			String testResultJson = RequestHelper.emitRequest(url, "POST", null, 200, false);
			TestResult testResult = (TestResult) SerializationHelper.getInstance().toClass(testResultJson, TestResult.class);

			//Make sure the results are as expected
			Assertions.assertTrue(testResult.isEqual());
		}

		//Edit output
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs/" + output1UUID;

			RequestHelper.emitRequest(url, "PUT", null, FileHelper.readFile("test-test-1", "output_1_edit.json"), MediaType.APPLICATION_JSON, 204, false);
		}

		//Run test
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/tests/" + testUUID;

			String testResultJson = RequestHelper.emitRequest(url, "POST", null, 200, true);
			TestResult testResult = (TestResult) SerializationHelper.getInstance().toClass(testResultJson, TestResult.class);

			//Make sure the results are as expected
			Assertions.assertFalse(testResult.isEqual());
		}

		//Edit test
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/tests/" + testUUID;

			test.setName("nameedit");
			test.setDescription("descriptionedit");
			test.setInput("inputedit");

			List<String> outputs = new LinkedList<>();
			outputs.add("outputedit1");
			outputs.add("outputedit2");
			test.setOutputs(outputs);

			//Serialize test object to JSON
			String testJson = SerializationHelper.getInstance().toJSON(test);

			//Edit test in workspace
			RequestHelper.emitRequest(url, "PUT", null, testJson, MediaType.APPLICATION_JSON, 204, false);
		}

		//Run test
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/tests/" + testUUID;

			//This should return 404 because the test's inputs and outputs reference objects that don't exist
			RequestHelper.emitRequest(url, "POST", null, 404, true);
		}

		//Get edited test indirectly, by getting all tests in the workspace
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/tests";

			String testsJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			HashMap<String, PersistedTest> tests = SerializationHelper.getInstance().toClass(testsJson, new TypeReference<HashMap<String, PersistedTest>>() {});

			Assertions.assertEquals(1, tests.size());

			//Compare to original test object
			PersistedTest testToCompare = tests.get(testUUID);
			Assertions.assertEquals(test.getName(), testToCompare.getName());
			Assertions.assertEquals(test.getDescription(), testToCompare.getDescription());
			Assertions.assertEquals(test.getInput(), testToCompare.getInput());
			Assertions.assertEquals(test.getOutputs(), testToCompare.getOutputs());
		}

		//Get edited test directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/tests/" + testUUID;

			String testJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			PersistedTest testToCompare = (PersistedTest) SerializationHelper.getInstance().toClass(testJson, PersistedTest.class);

			//Compare to original test object
			Assertions.assertEquals(test.getName(), testToCompare.getName());
			Assertions.assertEquals(test.getDescription(), testToCompare.getDescription());
			Assertions.assertEquals(test.getInput(), testToCompare.getInput());
			Assertions.assertEquals(test.getOutputs(), testToCompare.getOutputs());
		}

		//Delete test
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/tests/" + testUUID;

			RequestHelper.emitRequest(url, "DELETE", null, 204);
		}

		//Get test indirectly, by getting all tests in the workspace
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/tests/";

			String testJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			HashMap<String, PersistedTest> tests = SerializationHelper.getInstance().toClass(testJson, new TypeReference<HashMap<String, PersistedTest>>() {});

			//Make sure the deleted test was removed from the overall pool
			Assertions.assertNull(tests.get(testUUID));
			Assertions.assertEquals(0, tests.size());
		}

		//Get test directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/tests/" + testUUID;

			RequestHelper.emitRequest(url, "GET", null, 404);
		}
	}

	@AfterEach
	public void deleteWorkspace() {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);

		String urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID;

		String url = urlTemplate;
		RequestHelper.emitRequest(url, "DELETE", "test", 204);

		url = urlTemplate + "/public";
		RequestHelper.emitRequest(url, "GET", null, 404);
	}
}
