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

import de.materna.dmn.tester.servlets.playground.beans.Playground;
import de.materna.dmn.tester.FileHelper;
import de.materna.dmn.tester.ManagementHelper;
import de.materna.dmn.tester.RequestHelper;
import de.materna.jdec.serialization.SerializationHelper;

public class PlaygroundServletTest {
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
		String urlTemplate = declabHost + "/api/workspaces/" + randomUUID + "/playgrounds";
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
		
		//Check all non-playground-specific endpoints
		urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds";
		url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		RequestHelper.emitRequest(url, "POST", null, 415);
		RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 400, false);
		
		//Try creating invalid playgrounds
		String playgroundInvalid1Json = FileHelper.readFile("playground-test-1", "playground_invalid_1.json");
		RequestHelper.emitRequest(url, "POST", null, playgroundInvalid1Json, MediaType.APPLICATION_JSON, 400, false);
		
		String playgroundInvalid2Json = FileHelper.readFile("playground-test-1", "playground_invalid_2.json");
		RequestHelper.emitRequest(url, "POST", null, playgroundInvalid2Json, MediaType.APPLICATION_JSON, 400, false);
		
		String playgroundInvalid3Json = FileHelper.readFile("playground-test-1", "playground_invalid_3.json");;
		RequestHelper.emitRequest(url, "POST", null, playgroundInvalid3Json, MediaType.APPLICATION_JSON, 400, false);
		
		//Create valid playground
		String playgroundJson = FileHelper.readFile("playground-test-1", "playground_valid_1.json");
		String playgroundUUID = RequestHelper.emitRequest(url, "POST", null, playgroundJson, MediaType.APPLICATION_JSON, 201, true);
		
		//Check all playground-specific endpoints
		url += "/" + playgroundUUID;
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		RequestHelper.emitRequest(url, "PUT", null, 415);
		RequestHelper.emitRequest(url, "PUT", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 400, false);
		
		String playgroundEditJson = FileHelper.readFile("playground-test-1", "playground_valid_2.json");
		RequestHelper.emitRequest(url, "PUT", null, playgroundEditJson, MediaType.APPLICATION_JSON, 200, false);
		
		RequestHelper.emitRequest(url, "DELETE", null, 204);
		
		
		//Protected workspace
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
		String protectedWorkspaceJson = "{\"access\":\"PROTECTED\"}";
		RequestHelper.emitRequest(url, "POST", null, protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		RequestHelper.emitRequest(url, "POST", "test", protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		
		//Check all non-playground-specific endpoints
		urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds";
		url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		RequestHelper.emitRequest(url, "POST", "test", 415);
		RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		
		//Create valid playground
		RequestHelper.emitRequest(url, "POST", null, playgroundJson, MediaType.APPLICATION_JSON, 401, true);
		playgroundUUID = RequestHelper.emitRequest(url, "POST", "test", playgroundJson, MediaType.APPLICATION_JSON, 201, true);
		
		//Check all playground-specific endpoints
		url += "/" + playgroundUUID;
		
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		RequestHelper.emitRequest(url, "PUT", "test", 415);
		RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
		RequestHelper.emitRequest(url, "PUT", null, playgroundEditJson, MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", playgroundEditJson, MediaType.APPLICATION_JSON, 200, false);
		
		RequestHelper.emitRequest(url, "DELETE", null, 401);
		RequestHelper.emitRequest(url, "DELETE", "test", 204);
		
		
		//Private workspace
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
		String privateWorkspaceJson = "{\"access\":\"PRIVATE\"}";
		RequestHelper.emitRequest(url, "POST", null, privateWorkspaceJson, MediaType.APPLICATION_JSON, 401, true);
		RequestHelper.emitRequest(url, "POST", "test", privateWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		
		//Check all non-playground-specific endpoints
		urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds";
		url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 401);
		RequestHelper.emitRequest(url, "GET", "test", 200);
		
		RequestHelper.emitRequest(url, "POST", "test", 415);
		RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		
		//Create valid playground
		RequestHelper.emitRequest(url, "POST", null, playgroundJson, MediaType.APPLICATION_JSON, 401, true);
		playgroundUUID = RequestHelper.emitRequest(url, "POST", "test", playgroundJson, MediaType.APPLICATION_JSON, 201, true);
		
		//Check all playground-specific endpoints
		url += "/" + playgroundUUID;
		
		RequestHelper.emitRequest(url, "GET", null, 401);
		RequestHelper.emitRequest(url, "GET", "test", 200);

		RequestHelper.emitRequest(url, "PUT", "test", 415);
		RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
		RequestHelper.emitRequest(url, "PUT", null, playgroundEditJson, MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", playgroundEditJson, MediaType.APPLICATION_JSON, 200, false);
		
		RequestHelper.emitRequest(url, "DELETE", null, 401);
		RequestHelper.emitRequest(url, "DELETE", "test", 204);
		
		//Make workspace public again to be tidy
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
		RequestHelper.emitRequest(url, "POST", null, publicWorkspaceJson, MediaType.APPLICATION_JSON, 401, true);
		RequestHelper.emitRequest(url, "POST", "test", publicWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
	}
	
	@Test
	public void verifyFunctionality() throws URISyntaxException, IOException {
		Assertions.assertTrue(null != workspaceUUID && workspaceUUID.length() > 0);
		String url = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds/";
		
		//Create playground
		String playground1Json = FileHelper.readFile("playground-test-1", "playground_1.json");
		Playground playground1 = (Playground) SerializationHelper.getInstance().toClass(playground1Json, Playground.class);
		
		String playground1UUID = RequestHelper.emitRequest(url, "POST", null, playground1Json, MediaType.APPLICATION_JSON, 201, true);
		
		//Get playground
		url += playground1UUID;
		
		String playground1GetJson = RequestHelper.emitRequest(url, "GET", null, 200, false);
		
		//Make sure playground hasn't changed
		Playground playground1Get = (Playground) SerializationHelper.getInstance().toClass(playground1GetJson, Playground.class);
		Assertions.assertEquals(playground1Json, playground1GetJson);
		Assertions.assertEquals(playground1.getName(), playground1Get.getName());
		Assertions.assertEquals(playground1.getDescription(), playground1Get.getDescription());
		Assertions.assertEquals(playground1.getExpression(), playground1Get.getExpression());
		Assertions.assertEquals(playground1.getContext(), playground1Get.getContext());
		
		//Edit playground
		String playground1EditJson = FileHelper.readFile("playground-test-1", "playground_1_edit.json");
		Playground playground1Edit = (Playground) SerializationHelper.getInstance().toClass(playground1EditJson, Playground.class);
		
		RequestHelper.emitRequest(url, "PUT", null, playground1EditJson, MediaType.APPLICATION_JSON, 200, true);
		
		//Make sure playground has changed as expected
		String playground1EditGetJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
		Playground playground1EditGet = (Playground) SerializationHelper.getInstance().toClass(playground1EditGetJson, Playground.class);
		Assertions.assertEquals(playground1Edit.getName(), playground1EditGet.getName());
		Assertions.assertEquals(playground1Edit.getDescription(), playground1EditGet.getDescription());
		Assertions.assertEquals(playground1Edit.getExpression(), playground1EditGet.getExpression());
		Assertions.assertEquals(playground1Edit.getContext(), playground1EditGet.getContext());
		
		//Get all playgrounds
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds/";
		String playgroundsGetResponseString = RequestHelper.emitRequest(url, "GET", null, 200, false);
		HashMap<String, ?> playgrounds = SerializationHelper.getInstance().toClass(playgroundsGetResponseString, new TypeReference<HashMap<String, ?>>() {});
		Assertions.assertEquals(1, playgrounds.size());
		Assertions.assertNotEquals(null, playgrounds.get(playground1UUID));
		
		//Delete playground
		url += playground1UUID;
		RequestHelper.emitRequest(url, "DELETE", null, 204);
		
		//Check that the playground really was deleted
		RequestHelper.emitRequest(url, "GET", null, 404);
		
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds/";
		playgroundsGetResponseString = RequestHelper.emitRequest(url, "GET", null, 200, false);
		playgrounds = SerializationHelper.getInstance().toClass(playgroundsGetResponseString, new TypeReference<HashMap<String, ?>>() {});
		Assertions.assertTrue(0 == playgrounds.size() && null == playgrounds.get(playground1UUID));
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
