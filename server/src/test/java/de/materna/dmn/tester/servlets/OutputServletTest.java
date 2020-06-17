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
		String createdWorkspaceUUID = RequestHelper.emitRequest(declabHost + "/api/workspaces", "POST", null, FileHelper.readFile("workspace_public.json"), MediaType.APPLICATION_JSON, 200, true);
		Assertions.assertTrue(createdWorkspaceUUID != null && createdWorkspaceUUID.length() > 0);

		workspaceUUID = createdWorkspaceUUID;
	}

	@Test
	public void verifyResponseStatuses() throws URISyntaxException, IOException {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);

		String urlTemplate;
		String url;

		String outputUUID;

		String outputValid1Json = FileHelper.readFile("output-test-1", "output_valid_1.json");
		String outputValid2Json = FileHelper.readFile("output-test-1", "output_valid_2.json");

		//Check for non-existent outputs, make sure all endpoints handle as expected
		{
			String randomUUID = UUID.randomUUID().toString();
			urlTemplate = declabHost + "/api/workspaces/" + randomUUID + "/outputs";

			url = urlTemplate;
			RequestHelper.emitRequest(url, "GET", null, 404);
			RequestHelper.emitRequest(url, "POST", null, 415);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 404, false);

			url += "/" + randomUUID;
			RequestHelper.emitRequest(url, "GET", null, 404);
			RequestHelper.emitRequest(url, "PUT", null, 415);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 404, false);
			RequestHelper.emitRequest(url, "DELETE", null, 404);
		}

		//Make workspace public
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
			String publicWorkspaceJson = FileHelper.readFile("workspace_public.json");
			RequestHelper.emitRequest(url, "POST", null, publicWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		}

		//Check all non-output-specific endpoints in public workspace
		{
			urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs";

			url = urlTemplate;
			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "POST", null, 415);
			RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 400, false);
		}

		//Try creating invalid outputs in public workspace
		{
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("output-test-1", "output_invalid_1.json"), MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("output-test-1", "output_invalid_2.json"), MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("output-test-1", "output_invalid_3.json"), MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("output-test-1", "output_invalid_4.json"), MediaType.APPLICATION_JSON, 400, false);
		}

		//Create valid output in public workspace
		{
			outputUUID = RequestHelper.emitRequest(url, "POST", null, outputValid1Json, MediaType.APPLICATION_JSON, 201, true);
		}

		//Check all output-specific endpoints in public workspace
		{
			url += "/" + outputUUID;
			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "PUT", null, 415);
			RequestHelper.emitRequest(url, "PUT", null, "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "PUT", null, outputValid2Json, MediaType.APPLICATION_JSON, 204, false);
			RequestHelper.emitRequest(url, "DELETE", null, 204);
		}

		//Make workspace protected
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
			String protectedWorkspaceJson = FileHelper.readFile("workspace_protected.json");
			RequestHelper.emitRequest(url, "POST", null, protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
			RequestHelper.emitRequest(url, "POST", "test", protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		}

		//Check all non-output-specific endpoints in protected workspace
		{
			urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs";

			url = urlTemplate;
			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "POST", "test", 415);
			RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		}

		//Create valid output in protected workspace
		{
			RequestHelper.emitRequest(url, "POST", null, outputValid1Json, MediaType.APPLICATION_JSON, 401, true);
			outputUUID = RequestHelper.emitRequest(url, "POST", "test", outputValid1Json, MediaType.APPLICATION_JSON, 201, true);
		}

		//Check all output-specific endpoints in protected workspace
		{
			url += "/" + outputUUID;
			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "PUT", "test", 415);
			RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "PUT", null, outputValid2Json, MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", outputValid2Json, MediaType.APPLICATION_JSON, 204, false);
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

		//Check all non-output-specific endpoints in private workspace
		{
			urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs";

			url = urlTemplate;
			RequestHelper.emitRequest(url, "GET", null, 401);
			RequestHelper.emitRequest(url, "GET", "test", 200);
			RequestHelper.emitRequest(url, "POST", "test", 415);
			RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		}

		//Create valid output in private workspace
		{
			RequestHelper.emitRequest(url, "POST", null, outputValid1Json, MediaType.APPLICATION_JSON, 401, true);
			outputUUID = RequestHelper.emitRequest(url, "POST", "test", outputValid1Json, MediaType.APPLICATION_JSON, 201, true);
		}

		//Check all output-specific endpoints in private workspace
		{
			url += "/" + outputUUID;
			RequestHelper.emitRequest(url, "GET", null, 401);
			RequestHelper.emitRequest(url, "GET", "test", 200);
			RequestHelper.emitRequest(url, "PUT", "test", 415);
			RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "PUT", null, outputValid2Json, MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", outputValid2Json, MediaType.APPLICATION_JSON, 204, false);
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
	public void verifyFunctionality() throws URISyntaxException, IOException {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);

		String url;

		PersistedOutput output;
		String outputUUID;

		//Create output
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs/";

			String outputJson = FileHelper.readFile("output-test-1", "output_1.json");
			output = (PersistedOutput) SerializationHelper.getInstance().toClass(outputJson, PersistedOutput.class);
			outputUUID = RequestHelper.emitRequest(url, "POST", null, outputJson, MediaType.APPLICATION_JSON, 201, true);
		}

		//Get output indirectly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs";

			String outputsJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			HashMap<String, PersistedOutput> outputs = SerializationHelper.getInstance().toClass(outputsJson, new TypeReference<HashMap<String, PersistedOutput>>() {});

			Assertions.assertEquals(1, outputs.size());

			PersistedOutput outputToCompare = outputs.get(outputUUID);

			//Compare to the original output object
			Assertions.assertEquals(output.getName(), outputToCompare.getName());
			Assertions.assertEquals(output.getDecision(), outputToCompare.getDecision());
			Assertions.assertEquals(output.getValue(), outputToCompare.getValue());
		}

		//Get output directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs/" + outputUUID;

			String output1GetJson = RequestHelper.emitRequest(url, "GET", null, 200, true);

			PersistedOutput output1Get = (PersistedOutput) SerializationHelper.getInstance().toClass(output1GetJson, PersistedOutput.class);

			Assertions.assertEquals(output.getName(), output1Get.getName());
			Assertions.assertEquals(output.getDecision(), output1Get.getDecision());
			Assertions.assertEquals(output.getValue(), output1Get.getValue());
		}

		//Edit output
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs/" + outputUUID;

			String outputJson = FileHelper.readFile("output-test-1", "output_1_edit.json");
			output = (PersistedOutput) SerializationHelper.getInstance().toClass(outputJson, PersistedOutput.class);
			RequestHelper.emitRequest(url, "PUT", null, outputJson, MediaType.APPLICATION_JSON, 204, false);
		}

		//Get edited output indirectly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs/";

			String outputsJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			HashMap<String, PersistedOutput> outputs = SerializationHelper.getInstance().toClass(outputsJson, new TypeReference<HashMap<String, PersistedOutput>>() {});

			Assertions.assertEquals(1, outputs.size());

			PersistedOutput outputToCompare = outputs.get(outputUUID);

			//Compare to original output object
			Assertions.assertEquals(output.getName(), outputToCompare.getName());
			Assertions.assertEquals(output.getDecision(), outputToCompare.getDecision());
			Assertions.assertEquals(output.getValue(), outputToCompare.getValue());
		}

		//Get edited output directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs/" + outputUUID;

			String outputJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			PersistedOutput outputToCompare = (PersistedOutput) SerializationHelper.getInstance().toClass(outputJson, PersistedOutput.class);

			Assertions.assertEquals(output.getName(), outputToCompare.getName());
			Assertions.assertEquals(output.getDecision(), outputToCompare.getDecision());
			Assertions.assertEquals(output.getValue(), outputToCompare.getValue());
		}

		//Delete output
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs/" + outputUUID;

			RequestHelper.emitRequest(url, "DELETE", null, 204);
		}

		//Get deleted output indirectly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs/";

			String outputsJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			HashMap<String, PersistedOutput> outputs = SerializationHelper.getInstance().toClass(outputsJson, new TypeReference<HashMap<String, PersistedOutput>>() {});

			Assertions.assertEquals(0, outputs.size());
		}

		//Get deleted output directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/outputs/" + outputUUID;

			RequestHelper.emitRequest(url, "GET", null, 404);
		}
	}

	@AfterEach
	public void deleteWorkspace() {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);

		String urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID;

		String url = urlTemplate;
		RequestHelper.emitRequest(url, "DELETE", "test", 204);

		url += urlTemplate + "/public";
		RequestHelper.emitRequest(url, "GET", null, 404);
	}
}
