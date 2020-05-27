package de.materna.dmn.tester.servlets;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import de.materna.dmn.tester.TestHelper;
import de.materna.dmn.tester.servlets.test.beans.PersistedTest;
import de.materna.dmn.tester.servlets.test.beans.TestResult;
import de.materna.dmn.tester.servlets.test.beans.TestResultOutput;
import de.materna.jdec.serialization.SerializationHelper;

public class TestServletTest {
	static String declabHost = ManagementHelper.declabHost;
	static String workspaceUUID = "";
	
	@BeforeEach
	public void createWorkspace() throws URISyntaxException, IOException {
		//Create workspace
		String workspaceJson = FileHelper.readFile("workspace_public.json");
		String createdWorkspaceUUID = RequestHelper.emitRequest(declabHost + "/api/workspaces", "POST", null, workspaceJson, MediaType.APPLICATION_JSON, 200, true);
		Assertions.assertTrue(createdWorkspaceUUID != null && createdWorkspaceUUID.length() > 0);
		
		workspaceUUID = createdWorkspaceUUID;
	}
	
	@Test
	public void verifyResponseStatuses() throws URISyntaxException, IOException {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);
		
		//Check for non-existent workspaces, make sure all endpoints handle as expected
		String randomUUID = UUID.randomUUID().toString();
		String urlTemplate = declabHost + "/api/workspaces/" + randomUUID + "/tests";
		String url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 404);
		RequestHelper.emitRequest(url, "POST", null, 415);
		RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 404, false);
		
		url += "/" + randomUUID;
		RequestHelper.emitRequest(url, "GET", null, 404);
		RequestHelper.emitRequest(url, "POST", null, 404);
		RequestHelper.emitRequest(url, "PUT", null, 415);
		RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 404, false);
		RequestHelper.emitRequest(url, "DELETE", null, 404);
		
		//Public workspace
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
		String publicWorkspaceJson = "{\"access\":\"PUBLIC\",\"token\":\"test\"}";
		RequestHelper.emitRequest(url, "POST", null, publicWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		
		//Check all non-test-specific endpoints
		urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/tests";
		url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		RequestHelper.emitRequest(url, "POST", null, 415);
		RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 400, false);
		
		//Try creating invalid tests
		String testInvalid1Json = FileHelper.readFile("test-test-1", "test_invalid_1.json");
		RequestHelper.emitRequest(url, "POST", null, testInvalid1Json, MediaType.APPLICATION_JSON, 400, false);
		
		String testInvalid2Json = FileHelper.readFile("test-test-1", "test_invalid_2.json");
		RequestHelper.emitRequest(url, "POST", null, testInvalid2Json, MediaType.APPLICATION_JSON, 400, false);
		
		String testInvalid3Json = FileHelper.readFile("test-test-1", "test_invalid_3.json");
		RequestHelper.emitRequest(url, "POST", null, testInvalid3Json, MediaType.APPLICATION_JSON, 400, false);
		
		//Create valid test
		String testJson = FileHelper.readFile("test-test-1", "test_valid_1.json");
		String testUUID = RequestHelper.emitRequest(url, "POST", null, testJson, MediaType.APPLICATION_JSON, 201, true);
		
		//Check all test-specific endpoints
		url += "/" + testUUID;
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		RequestHelper.emitRequest(url, "POST", null, 503);
		
		RequestHelper.emitRequest(url, "PUT", null, 415);
		RequestHelper.emitRequest(url, "PUT", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 400, false);
		
		String testEditJson = FileHelper.readFile("test-test-1", "test_valid_2.json");
		RequestHelper.emitRequest(url, "PUT", null, testEditJson, MediaType.APPLICATION_JSON, 204, false);
		
		RequestHelper.emitRequest(url, "DELETE", null, 204);
		
		
		//Protected workspace
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
		String protectedWorkspaceJson = "{\"access\":\"PROTECTED\"}";
		RequestHelper.emitRequest(url, "POST", null, protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		RequestHelper.emitRequest(url, "POST", "test", protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		
		//Check all non-test-specific endpoints
		urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/tests";
		url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		RequestHelper.emitRequest(url, "POST", "test", 415);
		RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		
		//Create valid test
		RequestHelper.emitRequest(url, "POST", null, testJson, MediaType.APPLICATION_JSON, 401, true);
		testUUID = RequestHelper.emitRequest(url, "POST", "test", testJson, MediaType.APPLICATION_JSON, 201, true);
		
		//Check all test-specific endpoints
		url += "/" + testUUID;
		
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		RequestHelper.emitRequest(url, "POST", null, 401);
		RequestHelper.emitRequest(url, "POST", "test", 503);
		
		RequestHelper.emitRequest(url, "PUT", "test", 415);
		RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
		RequestHelper.emitRequest(url, "PUT", null, testEditJson, MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", testEditJson, MediaType.APPLICATION_JSON, 204, false);
		
		RequestHelper.emitRequest(url, "DELETE", null, 401);
		RequestHelper.emitRequest(url, "DELETE", "test", 204);
		
		
		//Private workspace
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
		String privateWorkspaceJson = "{\"access\":\"PRIVATE\"}";
		RequestHelper.emitRequest(url, "POST", null, privateWorkspaceJson, MediaType.APPLICATION_JSON, 401, true);
		RequestHelper.emitRequest(url, "POST", "test", privateWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		
		//Check all non-test-specific endpoints
		urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/tests";
		url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 401);
		RequestHelper.emitRequest(url, "GET", "test", 200);
		
		RequestHelper.emitRequest(url, "POST", "test", 415);
		RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		
		//Create valid test
		RequestHelper.emitRequest(url, "POST", null, testJson, MediaType.APPLICATION_JSON, 401, true);
		testUUID = RequestHelper.emitRequest(url, "POST", "test", testJson, MediaType.APPLICATION_JSON, 201, true);
		
		//Check all test-specific endpoints
		url += "/" + testUUID;
		
		RequestHelper.emitRequest(url, "GET", null, 401);
		RequestHelper.emitRequest(url, "GET", "test", 200);
		
		RequestHelper.emitRequest(url, "POST", null, 401);
		RequestHelper.emitRequest(url, "POST", "test", 503);

		RequestHelper.emitRequest(url, "PUT", "test", 415);
		RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
		RequestHelper.emitRequest(url, "PUT", null, testEditJson, MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", testEditJson, MediaType.APPLICATION_JSON, 204, false);
		
		RequestHelper.emitRequest(url, "DELETE", null, 401);
		RequestHelper.emitRequest(url, "DELETE", "test", 204);
		
		//Make workspace public again to be tidy
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
		RequestHelper.emitRequest(url, "POST", null, publicWorkspaceJson, MediaType.APPLICATION_JSON, 401, true);
		RequestHelper.emitRequest(url, "POST", "test", publicWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
	}
	
	@Test
	public void verifyFunctionality() throws URISyntaxException, UnsupportedEncodingException, IOException {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);
		
		//Navigate to test resources
		Path rootPath = TestHelper.getRootPath();
		File resourceDir = rootPath.resolve("scenarios").resolve("model-test-1").toFile();
		
		File models = new File(resourceDir.getPath() + File.separator + "anbieter_nutzer.json");
		Assertions.assertTrue(models.exists() && models.isFile() && models.canRead());
		String modelsJson = new String(Files.readAllBytes(models.toPath()), "UTF-8");

		String input1Json = FileHelper.readFile("test-test-1", "input_1.json");
		String output1Json = FileHelper.readFile("test-test-1", "output_1.json");
		String output2Json = FileHelper.readFile("test-test-1", "output_2.json");
		
		String urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID;
		
		//Import models
		String url = urlTemplate + "/model";
		RequestHelper.emitRequest(url, "PUT", null, modelsJson, MediaType.APPLICATION_JSON, 200, false);
		
		//Create input
		url = urlTemplate + "/inputs";
		String input1UUID = RequestHelper.emitRequest(url, "POST", null, input1Json, MediaType.APPLICATION_JSON,201, true);
		
		//Create outputs
		url = urlTemplate + "/outputs";
		String output1UUID = RequestHelper.emitRequest(url, "POST", null, output1Json, MediaType.APPLICATION_JSON, 201, true);
		String output2UUID = RequestHelper.emitRequest(url, "POST", null, output2Json, MediaType.APPLICATION_JSON, 201, true);
		
		List<String> outputs = new LinkedList<String>();
		outputs.add(output1UUID);
		outputs.add(output2UUID);
		
		//Create test JSON
		PersistedTest test1 = new PersistedTest();
		test1.setName("test1");
		test1.setDescription("description1");
		test1.setInput(input1UUID);
		test1.setOutputs(outputs);
		
		String test1Json = SerializationHelper.getInstance().toJSON(test1);
		
		//Create test
		url = urlTemplate + "/tests";
		String test1UUID = RequestHelper.emitRequest(url, "POST", null, test1Json, MediaType.APPLICATION_JSON, 201, true);
		
		//Get test indirectly
		String testsGet1Json = RequestHelper.emitRequest(url, "GET", null, 200, true);
		HashMap<String, PersistedTest> testsGet1 = SerializationHelper.getInstance().toClass(testsGet1Json, new TypeReference<HashMap<String, PersistedTest>>() {});
		
		Assertions.assertEquals(1, testsGet1.size());
		
		PersistedTest test1Indirect = testsGet1.get(test1UUID);
		
		Assertions.assertEquals(test1.getName(), test1Indirect.getName());
		Assertions.assertEquals(test1.getDescription(), test1Indirect.getDescription());
		Assertions.assertEquals(test1.getInput(), test1Indirect.getInput());
		Assertions.assertEquals(test1.getOutputs(), test1Indirect.getOutputs());
		
		//Get test directly
		url += "/" + test1UUID;
		
		String test1GetJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
		PersistedTest test1Get = (PersistedTest) SerializationHelper.getInstance().toClass(test1GetJson, PersistedTest.class);
		
		Assertions.assertEquals(test1.getName(), test1Get.getName());
		Assertions.assertEquals(test1.getDescription(), test1Get.getDescription());
		Assertions.assertEquals(test1.getInput(), test1Get.getInput());
		Assertions.assertEquals(test1.getOutputs(), test1Get.getOutputs());
		
		//Run test
		String testResult1Json = RequestHelper.emitRequest(url, "POST", null, 200, false);

		TestResult testResult1 = (TestResult) SerializationHelper.getInstance().toClass(testResult1Json, TestResult.class);
		Assertions.assertTrue(testResult1.isEqual());
		
		//Edit output
		String output1EditJson = FileHelper.readFile("test-test-1", "output_1_edit.json");
		
		url = urlTemplate + "/outputs/" + output1UUID;
		RequestHelper.emitRequest(url, "PUT", null, output1EditJson, MediaType.APPLICATION_JSON, 204, false);
		
		//Run test
		url = urlTemplate + "/tests/" + test1UUID;
		
		String testResult2Json = RequestHelper.emitRequest(url, "POST", null, 200, true);

		TestResult testResult2 = (TestResult) SerializationHelper.getInstance().toClass(testResult2Json, TestResult.class);
		Assertions.assertFalse(testResult2.isEqual());
		
		//Edit test
		test1.setName("nameedit");
		test1.setDescription("descriptionedit");
		test1.setInput("inputedit");
		
		outputs.set(0, "outputedit1");
		outputs.set(1, "outputedit2");
		test1.setOutputs(outputs);
		
		test1Json = SerializationHelper.getInstance().toJSON(test1);
		RequestHelper.emitRequest(url, "PUT", null, test1Json, MediaType.APPLICATION_JSON, 204, false);
		
		//Run test
		RequestHelper.emitRequest(url, "POST", null, 404, true);
		
		//Get edited test directly
		String test1EditGetJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
		PersistedTest test1EditGet = (PersistedTest) SerializationHelper.getInstance().toClass(test1EditGetJson, PersistedTest.class);
		
		Assertions.assertEquals(test1.getName(), test1EditGet.getName());
		Assertions.assertEquals(test1.getDescription(), test1EditGet.getDescription());
		Assertions.assertEquals(test1.getInput(), test1EditGet.getInput());
		Assertions.assertEquals(test1.getOutputs(), test1EditGet.getOutputs());
		
		//Get edited test indirectly
		url = urlTemplate + "/tests";
		String testsGet2Json = RequestHelper.emitRequest(url, "GET", null, 200, true);
		HashMap<String, PersistedTest> testsGet2 = SerializationHelper.getInstance().toClass(testsGet2Json, new TypeReference<HashMap<String, PersistedTest>>() {});
		
		PersistedTest test1EditIndirect = testsGet2.get(test1UUID);
		Assertions.assertEquals(test1.getName(), test1EditIndirect.getName());
		Assertions.assertEquals(test1.getDescription(), test1EditIndirect.getDescription());
		Assertions.assertEquals(test1.getInput(), test1EditIndirect.getInput());
		Assertions.assertEquals(test1.getOutputs(), test1EditIndirect.getOutputs());
		
		//Delete test
		url += "/" + test1UUID;
		
		RequestHelper.emitRequest(url, "DELETE", null, 204);
		
		//Get test directly
		RequestHelper.emitRequest(url, "GET", null, 404);
		
		//Get test indirectly
		url = urlTemplate + "/tests";
		String testsGet3Json = RequestHelper.emitRequest(url, "GET", null, 200, true);
		HashMap<String, PersistedTest> testsGet3 = SerializationHelper.getInstance().toClass(testsGet3Json, new TypeReference<HashMap<String, PersistedTest>>() {});
		
		Assertions.assertEquals(null, testsGet3.get(test1UUID));
		Assertions.assertEquals(0, testsGet3.size());
	}
	
	@AfterEach
	public void deleteWorkspace() {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);
		
		String url = declabHost + "/api/workspaces/" + workspaceUUID;
		RequestHelper.emitRequest(url, "DELETE", "test", 204);
		
		url += "/public";
		RequestHelper.emitRequest(url, "GET", null, 404);
	}
}
