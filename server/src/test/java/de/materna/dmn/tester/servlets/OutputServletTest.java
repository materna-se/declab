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

import de.materna.dmn.tester.FileHelper;
import de.materna.dmn.tester.ManagementHelper;
import de.materna.dmn.tester.RequestHelper;
import de.materna.dmn.tester.servlets.output.beans.PersistedOutput;
import de.materna.jdec.serialization.SerializationHelper;

public class OutputServletTest {
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
		String urlTemplate = declabHost + "/api/workspaces/" + randomUUID + "/outputs";
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
		
		//Check all non-output-specific endpoints
		urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs";
		url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		RequestHelper.emitRequest(url, "POST", null, 415);
		RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 400, false);
		
		//Try creating invalid outputs
		String outputInvalid1Json = FileHelper.readFile("output-test-1", "output_invalid_1.json");
		RequestHelper.emitRequest(url, "POST", null, outputInvalid1Json, MediaType.APPLICATION_JSON, 400, false);
		
		String outputInvalid2Json = FileHelper.readFile("output-test-1", "output_invalid_2.json");
		RequestHelper.emitRequest(url, "POST", null, outputInvalid2Json, MediaType.APPLICATION_JSON, 400, false);
		
		String outputInvalid3Json = FileHelper.readFile("output-test-1", "output_invalid_3.json");
		RequestHelper.emitRequest(url, "POST", null, outputInvalid3Json, MediaType.APPLICATION_JSON, 400, false);
		
		//Create valid output
		String outputJson = FileHelper.readFile("output-test-1", "output_valid_1.json");
		String outputUUID = RequestHelper.emitRequest(url, "POST", null, outputJson, MediaType.APPLICATION_JSON, 201, true);
		
		//Check all output-specific endpoints
		url += "/" + outputUUID;
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		RequestHelper.emitRequest(url, "PUT", null, 415);
		RequestHelper.emitRequest(url, "PUT", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 400, false);
		
		String outputEditJson = FileHelper.readFile("output-test-1", "output_valid_2.json");
		RequestHelper.emitRequest(url, "PUT", null, outputEditJson, MediaType.APPLICATION_JSON, 204, false);
		
		RequestHelper.emitRequest(url, "DELETE", null, 204);
		
		
		//Protected workspace
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
		String protectedWorkspaceJson = "{\"access\":\"PROTECTED\"}";
		RequestHelper.emitRequest(url, "POST", null, protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		RequestHelper.emitRequest(url, "POST", "test", protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		
		//Check all non-output-specific endpoints
		urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs";
		url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		RequestHelper.emitRequest(url, "POST", "test", 415);
		RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		
		//Create valid output
		RequestHelper.emitRequest(url, "POST", null, outputJson, MediaType.APPLICATION_JSON, 401, true);
		outputUUID = RequestHelper.emitRequest(url, "POST", "test", outputJson, MediaType.APPLICATION_JSON, 201, true);
		
		//Check all output-specific endpoints
		url += "/" + outputUUID;
		
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		RequestHelper.emitRequest(url, "PUT", "test", 415);
		RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
		RequestHelper.emitRequest(url, "PUT", null, outputEditJson, MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", outputEditJson, MediaType.APPLICATION_JSON, 204, false);
		
		RequestHelper.emitRequest(url, "DELETE", null, 401);
		RequestHelper.emitRequest(url, "DELETE", "test", 204);
		
		
		//Private workspace
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
		String privateWorkspaceJson = "{\"access\":\"PRIVATE\"}";
		RequestHelper.emitRequest(url, "POST", null, privateWorkspaceJson, MediaType.APPLICATION_JSON, 401, true);
		RequestHelper.emitRequest(url, "POST", "test", privateWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		
		//Check all non-output-specific endpoints
		urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs";
		url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 401);
		RequestHelper.emitRequest(url, "GET", "test", 200);
		
		RequestHelper.emitRequest(url, "POST", "test", 415);
		RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		
		//Create valid output
		RequestHelper.emitRequest(url, "POST", null, outputJson, MediaType.APPLICATION_JSON, 401, true);
		outputUUID = RequestHelper.emitRequest(url, "POST", "test", outputJson, MediaType.APPLICATION_JSON, 201, true);
		
		//Check all output-specific endpoints
		url += "/" + outputUUID;
		
		RequestHelper.emitRequest(url, "GET", null, 401);
		RequestHelper.emitRequest(url, "GET", "test", 200);

		RequestHelper.emitRequest(url, "PUT", "test", 415);
		RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
		RequestHelper.emitRequest(url, "PUT", null, outputEditJson, MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", outputEditJson, MediaType.APPLICATION_JSON, 204, false);
		
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

		String urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs";
		String url = urlTemplate;
		
		String output1Json = FileHelper.readFile("output-test-1", "output_1.json");
		PersistedOutput output1 = (PersistedOutput) SerializationHelper.getInstance().toClass(output1Json, PersistedOutput.class);
		String output1UUID = RequestHelper.emitRequest(url, "POST", null, output1Json, MediaType.APPLICATION_JSON, 201, true);
		
		//Get output indirectly
		String outputsGet1Json = RequestHelper.emitRequest(url, "GET", null, 200, true);
		HashMap<String, PersistedOutput> outputsGet1 = SerializationHelper.getInstance().toClass(outputsGet1Json, new TypeReference<HashMap<String, PersistedOutput>>() {});
		
		Assertions.assertEquals(1, outputsGet1.size());
		
		PersistedOutput output1Indirect = outputsGet1.get(output1UUID);
		
		Assertions.assertEquals(output1.getName(), output1Indirect.getName());
		Assertions.assertEquals(output1.getDecision(), output1Indirect.getDecision());
		Assertions.assertEquals(output1.getValue(), output1Indirect.getValue());
		
		//Get output directly
		url += "/" + output1UUID;
		String output1GetJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
		
		PersistedOutput output1Get = (PersistedOutput) SerializationHelper.getInstance().toClass(output1GetJson, PersistedOutput.class);
		
		Assertions.assertEquals(output1.getName(), output1Get.getName());
		Assertions.assertEquals(output1.getDecision(), output1Get.getDecision());
		Assertions.assertEquals(output1.getValue(), output1Get.getValue());
		
		//Edit output
		String output1EditJson = FileHelper.readFile("output-test-1", "output_1_edit.json");
		PersistedOutput output1Edit = (PersistedOutput) SerializationHelper.getInstance().toClass(output1EditJson, PersistedOutput.class);
		RequestHelper.emitRequest(url, "PUT", null, output1EditJson, MediaType.APPLICATION_JSON, 204, false);
		
		//Get edited output indirectly
		url = urlTemplate;
		String outputsGet2Json = RequestHelper.emitRequest(url, "GET", null, 200, true);
		HashMap<String, PersistedOutput> outputsGet2 = SerializationHelper.getInstance().toClass(outputsGet2Json, new TypeReference<HashMap<String, PersistedOutput>>() {});
		
		Assertions.assertEquals(1, outputsGet2.size());
		
		PersistedOutput output1EditIndirect = outputsGet2.get(output1UUID);
		
		Assertions.assertEquals(output1Edit.getName(), output1EditIndirect.getName());
		Assertions.assertEquals(output1Edit.getDecision(), output1EditIndirect.getDecision());
		Assertions.assertEquals(output1Edit.getValue(), output1EditIndirect.getValue());
		
		//Get edited output directly
		url += "/" + output1UUID;
		String output1EditGetJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
		PersistedOutput output1EditGet = (PersistedOutput) SerializationHelper.getInstance().toClass(output1EditGetJson, PersistedOutput.class);
		
		Assertions.assertEquals(output1Edit.getName(), output1EditGet.getName());
		Assertions.assertEquals(output1Edit.getDecision(), output1EditGet.getDecision());
		Assertions.assertEquals(output1Edit.getValue(), output1EditGet.getValue());
		
		//Delete output
		RequestHelper.emitRequest(url, "DELETE", null, 204);
		
		//Get outputs
		url = urlTemplate;
		String outputsGet3Json = RequestHelper.emitRequest(url, "GET", null, 200, true);
		HashMap<String, PersistedOutput> outputsGet3 = SerializationHelper.getInstance().toClass(outputsGet3Json, new TypeReference<HashMap<String, PersistedOutput>>() {});
		
		Assertions.assertEquals(0, outputsGet3.size());
		
		//Get deleted output directly
		url += "/" + output1UUID;
		
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
