package de.materna.dmn.tester.servlets;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import de.materna.dmn.tester.servlets.input.beans.PersistedInput;
import de.materna.dmn.tester.FileHelper;
import de.materna.dmn.tester.ManagementHelper;
import de.materna.dmn.tester.RequestHelper;
import de.materna.jdec.serialization.SerializationHelper;

public class InputServletTest {
	static String declabHost = ManagementHelper.declabHost;
	static String workspaceUUID = "";
	
	@BeforeEach
	public void createWorkspace() throws URISyntaxException, IOException {
		//Create workspace
		String workspaceJson = FileHelper.readFile("workspace_public.json");
		String createdWorkspaceUUID = RequestHelper.emitRequest(declabHost + "/api/workspaces", "POST", null, workspaceJson, MediaType.APPLICATION_JSON, 200, true);
		Assertions.assertTrue(createdWorkspaceUUID != null && createdWorkspaceUUID.length() > 0);
		
		String url = declabHost + "/api/workspaces/" + createdWorkspaceUUID + "/public";
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		workspaceUUID = createdWorkspaceUUID;
	}
	
	@Test
	public void verifyResponseStatuses() throws URISyntaxException, IOException {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);
		
		//Check for non-existent workspaces, make sure all endpoints handle as expected
		String randomUUID = UUID.randomUUID().toString();
		String urlTemplate = declabHost + "/api/workspaces/" + randomUUID + "/inputs";
		String url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 404);
		RequestHelper.emitRequest(url, "POST", null, 415);
		RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 404, false);
		
		url += "/" + randomUUID;
		RequestHelper.emitRequest(url, "GET", null, 404);
		RequestHelper.emitRequest(url, "PUT", null, 415);
		RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 404, false);
		RequestHelper.emitRequest(url, "DELETE", null, 404);
		
		//Public workspace
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
		String publicWorkspaceJson = "{\"access\":\"PUBLIC\",\"token\":\"test\"}";
		RequestHelper.emitRequest(url, "POST", null, publicWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		
		//Check all non-input-specific endpoints
		urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs";
		url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		RequestHelper.emitRequest(url, "POST", null, 415);
		RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 400, false);
		
		//Try creating invalid inputs
		String inputInvalid1Json = FileHelper.readFile("input-test-1", "input_invalid_1.json");
		RequestHelper.emitRequest(url, "POST", null, inputInvalid1Json, MediaType.APPLICATION_JSON, 400, false);
		
		String inputInvalid2Json = FileHelper.readFile("input-test-1", "input_invalid_2.json");
		RequestHelper.emitRequest(url, "POST", null, inputInvalid2Json, MediaType.APPLICATION_JSON, 400, false);
		
		String inputInvalid3Json = FileHelper.readFile("input-test-1", "input_invalid_3.json");
		RequestHelper.emitRequest(url, "POST", null, inputInvalid3Json, MediaType.APPLICATION_JSON, 400, false);
		
		//Create valid input
		String inputJson = FileHelper.readFile("input-test-1", "input_valid_1.json");
		String inputUUID = RequestHelper.emitRequest(url, "POST", null, inputJson, MediaType.APPLICATION_JSON, 201, true);
		
		//Check all input-specific endpoints
		url += "/" + inputUUID;
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		RequestHelper.emitRequest(url, "PUT", null, 415);
		RequestHelper.emitRequest(url, "PUT", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 400, false);
		
		String inputEditJson = FileHelper.readFile("input-test-1", "input_valid_2.json");
		RequestHelper.emitRequest(url, "PUT", null, inputEditJson, MediaType.APPLICATION_JSON, 204, false);
		
		RequestHelper.emitRequest(url, "DELETE", null, 204);
		
		//Protected workspace
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
		String protectedWorkspaceJson = "{\"access\":\"PROTECTED\"}";
		RequestHelper.emitRequest(url, "POST", null, protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		RequestHelper.emitRequest(url, "POST", "test", protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		
		//Check all non-input-specific endpoints
		urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs";
		url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		RequestHelper.emitRequest(url, "POST", "test", 415);
		RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		
		//Create valid input
		RequestHelper.emitRequest(url, "POST", null, inputJson, MediaType.APPLICATION_JSON, 401, true);
		inputUUID = RequestHelper.emitRequest(url, "POST", "test", inputJson, MediaType.APPLICATION_JSON, 201, true);
		
		//Check all input-specific endpoints
		url += "/" + inputUUID;
		
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		RequestHelper.emitRequest(url, "PUT", "test", 415);
		RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
		RequestHelper.emitRequest(url, "PUT", null, inputEditJson, MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", inputEditJson, MediaType.APPLICATION_JSON, 204, false);
		
		RequestHelper.emitRequest(url, "DELETE", null, 401);
		RequestHelper.emitRequest(url, "DELETE", "test", 204);
		
		
		//Private workspace
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
		String privateWorkspaceJson = "{\"access\":\"PRIVATE\"}";
		RequestHelper.emitRequest(url, "POST", null, privateWorkspaceJson, MediaType.APPLICATION_JSON, 401, true);
		RequestHelper.emitRequest(url, "POST", "test", privateWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		
		//Check all non-input-specific endpoints
		urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs";
		url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 401);
		RequestHelper.emitRequest(url, "GET", "test", 200);
		
		RequestHelper.emitRequest(url, "POST", "test", 415);
		RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		
		//Create valid input
		RequestHelper.emitRequest(url, "POST", null, inputJson, MediaType.APPLICATION_JSON, 401, true);
		inputUUID = RequestHelper.emitRequest(url, "POST", "test", inputJson, MediaType.APPLICATION_JSON, 201, true);
		
		//Check all input-specific endpoints
		url += "/" + inputUUID;
		
		RequestHelper.emitRequest(url, "GET", null, 401);
		RequestHelper.emitRequest(url, "GET", "test", 200);

		RequestHelper.emitRequest(url, "PUT", "test", 415);
		RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
		RequestHelper.emitRequest(url, "PUT", null, inputEditJson, MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", inputEditJson, MediaType.APPLICATION_JSON, 204, false);
		
		RequestHelper.emitRequest(url, "DELETE", null, 401);
		RequestHelper.emitRequest(url, "DELETE", "test", 204);
		
		//Make workspace public again to be tidy
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
		RequestHelper.emitRequest(url, "POST", null, publicWorkspaceJson, MediaType.APPLICATION_JSON, 401, true);
		RequestHelper.emitRequest(url, "POST", "test", publicWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
	}
	
	@Test
	public void verifyFunctionality() throws URISyntaxException, IOException {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);

		String url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/";
		
		//Create parent input
		String input1Json = FileHelper.readFile("input-test-1", "parent_input_1.json");
		PersistedInput input1 = (PersistedInput) SerializationHelper.getInstance().toClass(input1Json, PersistedInput.class);
		
		String input1UUID = RequestHelper.emitRequest(url, "POST", null, input1Json, MediaType.APPLICATION_JSON, 201, true);

		//Get parent input
		url += input1UUID;
		String input1GetBody = RequestHelper.emitRequest(url, "GET", null, 200, true);

		//Check that parent input has not changed
		PersistedInput input1Get = (PersistedInput) SerializationHelper.getInstance().toClass(input1GetBody, PersistedInput.class);
		Assertions.assertEquals(input1.getName(), input1Get.getName());
		Assertions.assertEquals(input1.getParent(), input1Get.getParent());
		Assertions.assertEquals(input1.getValue(), input1Get.getValue());
		
		//Check that exactly 1 input has been created
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/";
		String inputsGetBody = RequestHelper.emitRequest(url, "GET", null, 200, false);
		
		HashMap<String, ?> inputs = SerializationHelper.getInstance().toClass(inputsGetBody, new TypeReference<HashMap<String, ?>>() {});
		Assertions.assertTrue(1 == inputs.size() && null != inputs.get(input1UUID));
		
		//Create child input
		String input2Json = FileHelper.readFile("input-test-1", "child_input_1.json");
		PersistedInput input2 = (PersistedInput) SerializationHelper.getInstance().toClass(input2Json, PersistedInput.class);
		
		//Set correct parent UUID on child input
		input2.setParent(input1UUID);
		input2Json = SerializationHelper.getInstance().toJSON(input2);
		String input2UUID = RequestHelper.emitRequest(url, "POST", null, input2Json, MediaType.APPLICATION_JSON, 201, true);
		
		//Get child input
		url += input2UUID;
		String input2GetBody = RequestHelper.emitRequest(url, "GET", null, 200, true);
		
		//Check that child input has changed as expected
		PersistedInput input2Get = (PersistedInput) SerializationHelper.getInstance().toClass(input2GetBody, PersistedInput.class);
		Assertions.assertEquals(input2.getName(), input2Get.getName());
		Assertions.assertEquals(input2.getParent(), input2Get.getParent());
		Assertions.assertEquals(input2.getValue(), input2Get.getValue());
		
		url += "?merge=true";
		
		String input2GetMergedBody = RequestHelper.emitRequest(url, "GET", null, 200, true);
		PersistedInput input2GetMerged = (PersistedInput) SerializationHelper.getInstance().toClass(input2GetMergedBody, PersistedInput.class);
		Assertions.assertEquals(input2.getName(), input2GetMerged.getName());
		Assertions.assertEquals(input2.getParent(), input2GetMerged.getParent());
		Assertions.assertNotEquals(input2.getValue(), input2GetMerged.getValue());
		
		//Check that exactly 1 other input has been created
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/";
		inputsGetBody = RequestHelper.emitRequest(url, "GET", null, 200, false);
		
		inputs = SerializationHelper.getInstance().toClass(inputsGetBody, new TypeReference<HashMap<String, ?>>() {});
		Assertions.assertEquals(2, inputs.size());
		Assertions.assertNotEquals(null, inputs.get(input1UUID));
		Assertions.assertNotEquals(null, inputs.get(input2UUID));
		
		//Edit parent input
		url += input1UUID;
		
		String input1EditJson = FileHelper.readFile("input-test-1", "parent_input_2.json");
		PersistedInput input1Edit = (PersistedInput) SerializationHelper.getInstance().toClass(input1EditJson, PersistedInput.class);
		
		RequestHelper.emitRequest(url, "PUT", null, input1EditJson, MediaType.APPLICATION_JSON, 204, false);
	
		//Get edited parent input
		String input1EditGetBody = RequestHelper.emitRequest(url, "GET", null, 200, true);
		PersistedInput input1EditGet = (PersistedInput) SerializationHelper.getInstance().toClass(input1EditGetBody, PersistedInput.class);
		
		//Check that edited parent input was edited as expected
		Assertions.assertEquals(input1Edit.getName(), input1EditGet.getName());
		Assertions.assertEquals(input1Edit.getParent(), input1EditGet.getParent());
		Assertions.assertEquals(input1Edit.getValue(), input1EditGet.getValue());
		
		//Get edited child input
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/" + input2UUID;
		String input2EditGetBody = RequestHelper.emitRequest(url, "GET", null, 200, true);
		
		//Check that child input has changed as expected due to edited parent input
		PersistedInput input2EditGet = (PersistedInput) SerializationHelper.getInstance().toClass(input2EditGetBody, PersistedInput.class);
		Assertions.assertEquals(input2.getName(), input2EditGet.getName());
		Assertions.assertEquals(input2.getParent(), input2EditGet.getParent());
		Assertions.assertEquals(input2.getValue(), input2EditGet.getValue());
		
		//Get merged edited child input
		url += "/?merge=true";
		String input2EditGetMergedBody = RequestHelper.emitRequest(url, "GET", null, 200, true);
		
		//Check that child input has changed as expected due to edited parent input
		PersistedInput input2EditGetMerged = (PersistedInput) SerializationHelper.getInstance().toClass(input2EditGetMergedBody, PersistedInput.class);
		Assertions.assertEquals(input2.getName(), input2EditGetMerged.getName());
		Assertions.assertEquals(input2.getParent(), input2EditGetMerged.getParent());
		Assertions.assertNotEquals(input2.getValue(), input2EditGetMerged.getValue());
		
		//Delete child input
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/" + input2UUID;
		RequestHelper.emitRequest(url, "DELETE", null, 204);
		
		//Check that child input doesn't exist anymore
		RequestHelper.emitRequest(url, "GET", null, 404);
		
		//Check that there is only 1 input now
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/";
		inputsGetBody = RequestHelper.emitRequest(url, "GET", null, 200, false);
		
		inputs = SerializationHelper.getInstance().toClass(inputsGetBody, new TypeReference<HashMap<String, ?>>() {});
		Assertions.assertTrue(1 == inputs.size() && null != inputs.get(input1UUID));
		
		//Delete parent input
		url += input1UUID;
		RequestHelper.emitRequest(url, "DELETE", null, 204);
		
		//Check that parent input doesn't exist anymore
		RequestHelper.emitRequest(url, "GET", null, 404);
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
