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
		String createdWorkspaceUUID = RequestHelper.emitRequest(declabHost + "/api/workspaces", "POST", null, FileHelper.readFile("workspace_public.json"), MediaType.APPLICATION_JSON, 200, true);
		Assertions.assertTrue(createdWorkspaceUUID != null && createdWorkspaceUUID.length() > 0);

		workspaceUUID = createdWorkspaceUUID;
	}

	@Test
	public void verifyResponseStatuses() throws URISyntaxException, IOException {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);

		String urlTemplate;
		String url;

		String inputUUID;

		String inputValid1Json = FileHelper.readFile("input-test-1", "input_valid_1.json");
		String inputValid2Json = FileHelper.readFile("input-test-1", "input_valid_2.json");

		//Check for non-existent inputs, make sure all endpoints handle as expected
		{
			String randomUUID = UUID.randomUUID().toString();
			urlTemplate = declabHost + "/api/workspaces/" + randomUUID + "/inputs";

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

		//Check all non-input-specific endpoints in public workspace
		{
			urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs";

			url = urlTemplate;
			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "POST", null, 415);
			RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 400, false);
		}

		//Try creating invalid inputs in public workspace
		{
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("input-test-1", "input_invalid_1.json"), MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("input-test-1", "input_invalid_2.json"), MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("input-test-1", "input_invalid_3.json"), MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("input-test-1", "input_invalid_4.json"), MediaType.APPLICATION_JSON, 400, false);
		}

		//Create valid input in public workspace
		{
			inputUUID = RequestHelper.emitRequest(url, "POST", null, inputValid1Json, MediaType.APPLICATION_JSON, 201, true);
		}

		//Check all input-specific endpoints in public workspace
		{
			url += "/" + inputUUID;
			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "PUT", null, 415);
			RequestHelper.emitRequest(url, "PUT", null, "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "PUT", null, inputValid2Json, MediaType.APPLICATION_JSON, 204, false);
			RequestHelper.emitRequest(url, "DELETE", null, 204);
		}

		//Make workspace protected
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
			String protectedWorkspaceJson = FileHelper.readFile("workspace_protected.json");
			RequestHelper.emitRequest(url, "POST", null, protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
			RequestHelper.emitRequest(url, "POST", "test", protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		}

		//Check all non-input-specific endpoints in protected workspace
		{
			urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs";

			url = urlTemplate;
			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "POST", "test", 415);
			RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		}

		//Create valid input in protected workspace
		{
			RequestHelper.emitRequest(url, "POST", null, inputValid1Json, MediaType.APPLICATION_JSON, 401, true);
			inputUUID = RequestHelper.emitRequest(url, "POST", "test", inputValid1Json, MediaType.APPLICATION_JSON, 201, true);
		}

		//Check all input-specific endpoints in protected workspace
		{
			url += "/" + inputUUID;
			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "PUT", "test", 415);
			RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "PUT", null, inputValid2Json, MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", inputValid2Json, MediaType.APPLICATION_JSON, 204, false);
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

		//Check all non-input-specific endpoints in private workspace
		{
			urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs";

			url = urlTemplate;
			RequestHelper.emitRequest(url, "GET", null, 401);
			RequestHelper.emitRequest(url, "GET", "test", 200);
			RequestHelper.emitRequest(url, "POST", "test", 415);
			RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		}

		//Create valid input in private workspace
		{
			RequestHelper.emitRequest(url, "POST", null, inputValid1Json, MediaType.APPLICATION_JSON, 401, true);
			inputUUID = RequestHelper.emitRequest(url, "POST", "test", inputValid1Json, MediaType.APPLICATION_JSON, 201, true);
		}

		//Check all input-specific endpoints in private workspace
		{
			url += "/" + inputUUID;
			RequestHelper.emitRequest(url, "GET", null, 401);
			RequestHelper.emitRequest(url, "GET", "test", 200);
			RequestHelper.emitRequest(url, "PUT", "test", 415);
			RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "PUT", null, inputValid2Json, MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", inputValid2Json, MediaType.APPLICATION_JSON, 204, false);
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

		String inputParentUUID, inputChildUUID;

		String inputParentJson, inputChildJson;

		PersistedInput inputParent, inputChild;

		String url;

		//Create parent input
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/";

			inputParentJson = FileHelper.readFile("input-test-1", "parent_input_1.json");
			inputParent = (PersistedInput) SerializationHelper.getInstance().toClass(inputParentJson, PersistedInput.class);

			inputParentUUID = RequestHelper.emitRequest(url, "POST", null, inputParentJson, MediaType.APPLICATION_JSON, 201, true);
		}

		//Get parent input indirectly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/";
			String inputsJson = RequestHelper.emitRequest(url, "GET", null, 200, false);

			HashMap<String, PersistedInput> inputs = SerializationHelper.getInstance().toClass(inputsJson, new TypeReference<HashMap<String, PersistedInput>>() {});
			Assertions.assertEquals(1, inputs.size());

			PersistedInput inputToCompare = inputs.get(inputParentUUID);

			//Compare to original parent input object
			Assertions.assertEquals(inputParent.getName(), inputToCompare.getName());
			Assertions.assertEquals(inputParent.getParent(), inputToCompare.getParent());
			Assertions.assertEquals(inputParent.getValue(), inputToCompare.getValue());
		}

		//Get parent input directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/" + inputParentUUID;
			String inputJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			PersistedInput inputToCompare = (PersistedInput) SerializationHelper.getInstance().toClass(inputJson, PersistedInput.class);

			//Compare to original parent input object
			Assertions.assertEquals(inputParent.getName(), inputToCompare.getName());
			Assertions.assertEquals(inputParent.getParent(), inputToCompare.getParent());
			Assertions.assertEquals(inputParent.getValue(), inputToCompare.getValue());
		}

		//Create child input
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/";

			inputChildJson = FileHelper.readFile("input-test-1", "child_input_1.json");
			inputChild = (PersistedInput) SerializationHelper.getInstance().toClass(inputChildJson, PersistedInput.class);

			//Set correct parent UUID on child input
			inputChild.setParent(inputParentUUID);

			inputChildJson = SerializationHelper.getInstance().toJSON(inputChild);
			inputChildUUID = RequestHelper.emitRequest(url, "POST", null, inputChildJson, MediaType.APPLICATION_JSON, 201, true);
		}

		//Get child input indirectly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/";
			String inputsJson = RequestHelper.emitRequest(url, "GET", null, 200, false);

			HashMap<String, PersistedInput> inputs = SerializationHelper.getInstance().toClass(inputsJson, new TypeReference<HashMap<String, PersistedInput>>() {});
			Assertions.assertEquals(2, inputs.size());

			PersistedInput inputToCompare = inputs.get(inputChildUUID);

			//Compare to original child input object
			Assertions.assertEquals(inputChild.getName(), inputToCompare.getName());
			Assertions.assertEquals(inputChild.getParent(), inputToCompare.getParent());
			Assertions.assertEquals(inputChild.getValue(), inputToCompare.getValue());
		}

		//Get child input directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/" + inputChildUUID;
			String inputJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			PersistedInput inputToCompare = (PersistedInput) SerializationHelper.getInstance().toClass(inputJson, PersistedInput.class);

			//Compare to original child input object
			Assertions.assertEquals(inputChild.getName(), inputToCompare.getName());
			Assertions.assertEquals(inputChild.getParent(), inputToCompare.getParent());
			Assertions.assertEquals(inputChild.getValue(), inputToCompare.getValue());
		}

		//Get merged child input directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/" + inputChildUUID + "?merge=true";

			String inputJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			PersistedInput inputToCompare = (PersistedInput) SerializationHelper.getInstance().toClass(inputJson, PersistedInput.class);
			Assertions.assertEquals(inputChild.getName(), inputToCompare.getName());
			Assertions.assertEquals(inputChild.getParent(), inputToCompare.getParent());
			Assertions.assertNotEquals(inputChild.getValue(), inputToCompare.getValue());
		}

		//Edit parent input
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/" + inputParentUUID;

			inputParentJson = FileHelper.readFile("input-test-1", "parent_input_2.json");
			inputParent = (PersistedInput) SerializationHelper.getInstance().toClass(inputParentJson, PersistedInput.class);

			RequestHelper.emitRequest(url, "PUT", null, inputParentJson, MediaType.APPLICATION_JSON, 204, false);
		}

		//Get edited parent input directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/" + inputParentUUID;

			String inputJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			PersistedInput inputToCompare = (PersistedInput) SerializationHelper.getInstance().toClass(inputJson, PersistedInput.class);

			//Compare to original parent input object
			Assertions.assertEquals(inputParent.getName(), inputToCompare.getName());
			Assertions.assertEquals(inputParent.getParent(), inputToCompare.getParent());
			Assertions.assertEquals(inputParent.getValue(), inputToCompare.getValue());
		}

		//Get edited child input directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/" + inputChildUUID;

			String inputJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			PersistedInput inputToCompare = (PersistedInput) SerializationHelper.getInstance().toClass(inputJson, PersistedInput.class);

			//Compare to original child input object
			//Without merging, the child input should not change even if the parent input changes
			Assertions.assertEquals(inputChild.getName(), inputToCompare.getName());
			Assertions.assertEquals(inputChild.getParent(), inputToCompare.getParent());
			Assertions.assertEquals(inputChild.getValue(), inputToCompare.getValue());
		}

		//Get merged edited child input directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/" + inputChildUUID + "/?merge=true";

			String inputJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			PersistedInput inputToCompare = (PersistedInput) SerializationHelper.getInstance().toClass(inputJson, PersistedInput.class);

			//Compare to original child input object
			//Merging the parent and child inputs should have changed the child input's value
			Assertions.assertEquals(inputChild.getName(), inputToCompare.getName());
			Assertions.assertEquals(inputChild.getParent(), inputToCompare.getParent());
			Assertions.assertNotEquals(inputChild.getValue(), inputToCompare.getValue());
		}

		//Delete child input
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/" + inputChildUUID;

			RequestHelper.emitRequest(url, "DELETE", null, 204);

			RequestHelper.emitRequest(url, "GET", null, 404);
		}

		//Check that there is only 1 input now
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/";

			String inputsJson = RequestHelper.emitRequest(url, "GET", null, 200, false);

			HashMap<String, PersistedInput> inputs = SerializationHelper.getInstance().toClass(inputsJson, new TypeReference<HashMap<String, PersistedInput>>() {});
			Assertions.assertTrue(1 == inputs.size() && null != inputs.get(inputParentUUID));
		}

		//Delete parent input
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/" + inputParentUUID;

			RequestHelper.emitRequest(url, "DELETE", null, 204);

			RequestHelper.emitRequest(url, "GET", null, 404);
		}

		//Check that there are no inputs left
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/inputs/";

			String inputsJson = RequestHelper.emitRequest(url, "GET", null, 200, false);

			HashMap<String, PersistedInput> inputs = SerializationHelper.getInstance().toClass(inputsJson, new TypeReference<HashMap<String, PersistedInput>>() {});
			Assertions.assertEquals(0, inputs.size());
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
