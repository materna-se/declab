package de.materna.dmn.tester.servlets;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
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
import de.materna.dmn.tester.servlets.model.beans.Model;
import de.materna.jdec.model.ExecutionResult;
import de.materna.jdec.serialization.SerializationHelper;

public class ModelServletTest {
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
		
		String model1Json = FileHelper.readFile("model-test-1", "anbieter.json");
		String input1Json = FileHelper.readFile("model-test-1", "nutzer.dmn");
		
		//Check for non-existent workspaces, make sure all endpoints handle as expected
		String randomUUID = UUID.randomUUID().toString();
		String urlTemplate = declabHost + "/api/workspaces/" + randomUUID + "/model";
		String url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 404);
		RequestHelper.emitRequest(url, "PUT", null, 415);
		RequestHelper.emitRequest(url, "PUT", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "PUT", null, "{}", MediaType.APPLICATION_JSON, 404, false);
		
		url = urlTemplate + "/inputs";
		
		RequestHelper.emitRequest(url, "GET", null, 404);
		
		url = urlTemplate + "/execute";
		RequestHelper.emitRequest(url, "POST", null, 415);
		RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.APPLICATION_JSON, 404, false);
		
		url = urlTemplate + "/execute/raw";
		RequestHelper.emitRequest(url, "POST", null, 415);
		RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.APPLICATION_JSON, 404, false);
		
		//Public workspace
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
		String publicWorkspaceJson = "{\"access\":\"PUBLIC\",\"token\":\"test\"}";
		RequestHelper.emitRequest(url, "POST", null, publicWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		
		//Check all endpoints
		urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/model";
		url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 200);
		RequestHelper.emitRequest(url, "PUT", null, 415);
		RequestHelper.emitRequest(url, "PUT", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "PUT", null, "{_}", MediaType.APPLICATION_JSON, 400, false);
		RequestHelper.emitRequest(url, "PUT", null, model1Json, MediaType.APPLICATION_JSON, 200, false);
		
		url = urlTemplate + "/inputs";
		
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		url = urlTemplate + "/execute";
		RequestHelper.emitRequest(url, "POST", null, 415);
		RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "{_}", MediaType.APPLICATION_JSON, 400, false);
		RequestHelper.emitRequest(url, "POST", null, input1Json, MediaType.APPLICATION_JSON, 200, false);
		
		url = urlTemplate + "/execute/raw";
		RequestHelper.emitRequest(url, "POST", null, 415);
		RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "{_}", MediaType.APPLICATION_JSON, 400, false);
		
		//Protected workspace
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
		String protectedWorkspaceJson = "{\"access\":\"PROTECTED\"}";
		RequestHelper.emitRequest(url, "POST", null, protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		
		//Check all endpoints
		urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/model";
		url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 200);
		RequestHelper.emitRequest(url, "PUT", null, 415);
		RequestHelper.emitRequest(url, "PUT", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "PUT", null, "{_}", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", "{_}", MediaType.APPLICATION_JSON, 400, false);
		RequestHelper.emitRequest(url, "PUT", null, model1Json, MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", model1Json, MediaType.APPLICATION_JSON, 200, false);
		
		url = urlTemplate + "/inputs";
		
		RequestHelper.emitRequest(url, "GET", null, 200);
		
		url = urlTemplate + "/execute";
		RequestHelper.emitRequest(url, "POST", null, 415);
		RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "{_}", MediaType.APPLICATION_JSON, 400, false);
		RequestHelper.emitRequest(url, "POST", null, input1Json, MediaType.APPLICATION_JSON, 200, false);
		
		url = urlTemplate + "/execute/raw";
		RequestHelper.emitRequest(url, "POST", null, 415);
		RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "{_}", MediaType.APPLICATION_JSON, 400, false);
		
		//Private workspace
		url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
		String privateWorkspaceJson = "{\"access\":\"PRIVATE\"}";
		RequestHelper.emitRequest(url, "POST", null, privateWorkspaceJson, MediaType.APPLICATION_JSON, 401, true);
		RequestHelper.emitRequest(url, "POST", "test", privateWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		
		//Check all endpoints
		urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/model";
		url = urlTemplate;
		
		RequestHelper.emitRequest(url, "GET", null, 401);
		RequestHelper.emitRequest(url, "GET", "test", 200);
		RequestHelper.emitRequest(url, "PUT", null, 415);
		RequestHelper.emitRequest(url, "PUT", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "PUT", null, "{_}", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", "{_}", MediaType.APPLICATION_JSON, 400, false);
		RequestHelper.emitRequest(url, "PUT", null, model1Json, MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "PUT", "test", model1Json, MediaType.APPLICATION_JSON, 200, false);
		
		url = urlTemplate + "/inputs";
		
		RequestHelper.emitRequest(url, "GET", null, 401);
		RequestHelper.emitRequest(url, "GET", "test", 200);
		
		url = urlTemplate + "/execute";
		RequestHelper.emitRequest(url, "POST", null, 415);
		RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "{_}", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "POST", "test", "{_}", MediaType.APPLICATION_JSON, 400, false);
		RequestHelper.emitRequest(url, "POST", null, input1Json, MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "POST", "test", input1Json, MediaType.APPLICATION_JSON, 200, false);
		
		url = urlTemplate + "/execute/raw";
		RequestHelper.emitRequest(url, "POST", null, 415);
		RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
		RequestHelper.emitRequest(url, "POST", null, "{_}", MediaType.APPLICATION_JSON, 401, false);
		RequestHelper.emitRequest(url, "POST", "test", "{_}", MediaType.APPLICATION_JSON, 400, false);
	}
	
	@Test
	public void verifyFunctionality() throws URISyntaxException, UnsupportedEncodingException, IOException {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);
		
		String model1Json = FileHelper.readFile("model-test-1", "anbieter.json");
		String model2Json = FileHelper.readFile("model-test-1", "nutzer.json");
		String model3Json = FileHelper.readFile("model-test-1", "anbieter_nutzer.json");
		String input1Json = FileHelper.readFile("model-test-1", "input.json");

		String urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/model";
		String url = urlTemplate;
		
		//Import model
		RequestHelper.emitRequest(url, "PUT", null, model1Json, MediaType.APPLICATION_JSON, 200, false);
		
		LinkedList<Model> modelsGet1 = SerializationHelper.getInstance().toClass(RequestHelper.emitRequest(url, "GET", null, 200, true), new TypeReference<LinkedList<Model>>() {});
		Assertions.assertEquals(1, modelsGet1.size());
		
		//Try to import model without required dependency
		RequestHelper.emitRequest(url, "PUT", null, model2Json, MediaType.APPLICATION_JSON, 400, false);
		
		LinkedList<Model> modelsGet2 = SerializationHelper.getInstance().toClass(RequestHelper.emitRequest(url, "GET", null, 200, true), new TypeReference<LinkedList<Model>>() {});
		Assertions.assertEquals(modelsGet1.size(), modelsGet2.size());
		Assertions.assertEquals(SerializationHelper.getInstance().toJSON(modelsGet1), SerializationHelper.getInstance().toJSON(modelsGet2));
		
		//Import both models
		RequestHelper.emitRequest(url, "PUT", null, model3Json, MediaType.APPLICATION_JSON, 200, false);
		
		LinkedList<Model> modelsGet3 = SerializationHelper.getInstance().toClass(RequestHelper.emitRequest(url, "GET", null, 200, true), new TypeReference<LinkedList<Model>>() {});
		Assertions.assertEquals(2, modelsGet3.size());
		
		//Run test
		url = urlTemplate + "/execute";
		
		System.out.println("Sending input json: " + input1Json);
		
		ExecutionResult result = (ExecutionResult) SerializationHelper.getInstance().toClass(RequestHelper.emitRequest(url, "POST", null, input1Json, MediaType.APPLICATION_JSON, 200, true), ExecutionResult.class);
		System.out.println("Results: " + SerializationHelper.getInstance().toJSON(result));
		Assertions.assertEquals(1, result.getOutputs().get("anbieter.danbieter"));
		Assertions.assertEquals(13, result.getOutputs().get("dnutzer"));
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
