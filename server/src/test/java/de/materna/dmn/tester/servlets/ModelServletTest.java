package de.materna.dmn.tester.servlets;

import com.fasterxml.jackson.core.type.TypeReference;
import de.materna.dmn.tester.FileHelper;
import de.materna.dmn.tester.ManagementHelper;
import de.materna.dmn.tester.RequestHelper;
import de.materna.jdec.model.ExecutionResult;
import de.materna.jdec.model.Model;
import de.materna.jdec.serialization.SerializationHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.UUID;

public class ModelServletTest {
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

		String model1Json = FileHelper.readFile("model-test-1", "anbieter.json");
		String input1Json = FileHelper.readFile("model-test-1", "nutzer.dmn");

		//Check for non-existent models, make sure all endpoints handle as expected
		{
			String randomUUID = UUID.randomUUID().toString();
			urlTemplate = declabHost + "/api/workspaces/" + randomUUID + "/model";
			url = urlTemplate;

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
		}

		//Make workspace public
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
			String publicWorkspaceJson = FileHelper.readFile("workspace_public.json");
			RequestHelper.emitRequest(url, "POST", null, publicWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		}

		//Check all endpoints in public workspace
		{
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
		}

		//Make workspace protected
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
			String protectedWorkspaceJson = FileHelper.readFile("workspace_protected.json");
			RequestHelper.emitRequest(url, "POST", null, protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		}

		//Check all endpoints in protected workspace
		{
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
		}

		//Make workspace private
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
			String privateWorkspaceJson = FileHelper.readFile("workspace_private.json");
			RequestHelper.emitRequest(url, "POST", null, privateWorkspaceJson, MediaType.APPLICATION_JSON, 401, true);
			RequestHelper.emitRequest(url, "POST", "test", privateWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		}

		//Check all endpoints in private workspace
		{
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
	}

	@Test
	public void verifyFunctionality() throws URISyntaxException, UnsupportedEncodingException, IOException {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);

		String model1Json = FileHelper.readFile("model-test-1", "anbieter.json");
		String model2Json = FileHelper.readFile("model-test-1", "nutzer.json");
		String model3Json = FileHelper.readFile("model-test-1", "anbieter_nutzer.json");
		String input1Json = FileHelper.readFile("model-test-1", "input.json");

		String modelsBackup;

		String url;

		//Import model
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/model/";

			RequestHelper.emitRequest(url, "PUT", null, model1Json, MediaType.APPLICATION_JSON, 200, false);

			LinkedList<Model> models = SerializationHelper.getInstance().toClass(RequestHelper.emitRequest(url, "GET", null, 200, true), new TypeReference<LinkedList<Model>>() {
			});
			Assertions.assertEquals(1, models.size());
		}

		//Try to import model without required dependency
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/model/";

			RequestHelper.emitRequest(url, "PUT", null, model2Json, MediaType.APPLICATION_JSON, 400, false);

			LinkedList<Model> models = SerializationHelper.getInstance().toClass(RequestHelper.emitRequest(url, "GET", null, 200, true), new TypeReference<LinkedList<Model>>() {
			});

			//The only model in the list should be the anbieter model, since the nutzer model got rejected
			Assertions.assertEquals(1, models.size());
			Assertions.assertEquals("anbieter", models.getFirst().getName());
		}

		//Import both models
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/model/";

			RequestHelper.emitRequest(url, "PUT", null, model3Json, MediaType.APPLICATION_JSON, 200, false);

			LinkedList<Model> models = SerializationHelper.getInstance().toClass(RequestHelper.emitRequest(url, "GET", null, 200, true), new TypeReference<LinkedList<Model>>() {
			});
			Assertions.assertEquals(2, models.size());
		}

		//Run test
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/model/execute";

			ExecutionResult result = (ExecutionResult) SerializationHelper.getInstance().toClass(RequestHelper.emitRequest(url, "POST", null, input1Json, MediaType.APPLICATION_JSON, 200, true), ExecutionResult.class);

			//Compare calculated results to expected results
			Assertions.assertEquals(1, result.getOutputs().get("anbieter.danbieter"));
			Assertions.assertEquals(13, result.getOutputs().get("dnutzer"));
		}
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
