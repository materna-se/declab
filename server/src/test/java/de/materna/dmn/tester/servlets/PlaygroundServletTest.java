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
		String createdWorkspaceUUID = RequestHelper.emitRequest(declabHost + "/api/workspaces", "POST", null, FileHelper.readFile("workspace_public.json"), MediaType.APPLICATION_JSON, 200, true);
		Assertions.assertTrue(createdWorkspaceUUID != null && createdWorkspaceUUID.length() > 0);

		workspaceUUID = createdWorkspaceUUID;
	}

	@Test
	public void verifyResponseStatuses() throws URISyntaxException, IOException {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);

		String urlTemplate;
		String url;

		String playgroundUUID;

		String playgroundValid1Json = FileHelper.readFile("playground-test-1", "playground_valid_1.json");
		String playgroundValid2Json = FileHelper.readFile("playground-test-1", "playground_valid_2.json");


		//Check for non-existent playgrounds, make sure all endpoints handle as expected
		{
			String randomUUID = UUID.randomUUID().toString();
			urlTemplate = declabHost + "/api/workspaces/" + randomUUID + "/playgrounds";

			url = urlTemplate;
			RequestHelper.emitRequest(url, "GET", null, 404);
			RequestHelper.emitRequest(url, "POST", null, 415);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 404, false);

			url = urlTemplate + "/" + randomUUID;
			RequestHelper.emitRequest(url, "GET", null, 404);
			RequestHelper.emitRequest(url, "PUT", null, 415);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 404, false);
			RequestHelper.emitRequest(url, "DELETE", null, 404);
		}

		//Make workspace public
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("workspace_public.json"), MediaType.APPLICATION_JSON, 200, true);
		}

		//Check all non-playground-specific endpoints in public workspace
		{
			urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds";

			url = urlTemplate;
			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "POST", null, 415);
			RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 400, false);
		}

		//Try creating invalid playgrounds in public workspace
		{
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("playground-test-1", "playground_invalid_1.json"), MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("playground-test-1", "playground_invalid_2.json"), MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("playground-test-1", "playground_invalid_3.json"), MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("playground-test-1", "playground_invalid_4.json"), MediaType.APPLICATION_JSON, 400, false);
		}

		//Create valid playground in public workspace
		{
			playgroundUUID = RequestHelper.emitRequest(url, "POST", null, playgroundValid1Json, MediaType.APPLICATION_JSON, 201, true);
		}

		//Check all playground-specific endpoints
		{
			url = urlTemplate + "/" + playgroundUUID;
			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "PUT", null, 415);
			RequestHelper.emitRequest(url, "PUT", null, "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "PUT", null, playgroundValid2Json, MediaType.APPLICATION_JSON, 200, false);
			RequestHelper.emitRequest(url, "DELETE", null, 204);
		}

		//Make workspace protected
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
			String protectedWorkspaceJson = FileHelper.readFile("workspace_protected.json");
			RequestHelper.emitRequest(url, "POST", null, protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
			RequestHelper.emitRequest(url, "POST", "test", protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		}

		//Check all non-playground-specific endpoints in protected workspace
		{
			urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds";

			url = urlTemplate;
			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "POST", "test", 415);
			RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		}

		//Create valid playground in protected workspace
		{
			RequestHelper.emitRequest(url, "POST", null, playgroundValid1Json, MediaType.APPLICATION_JSON, 401, true);
			playgroundUUID = RequestHelper.emitRequest(url, "POST", "test", playgroundValid1Json, MediaType.APPLICATION_JSON, 201, true);
		}

		//Check all playground-specific endpoints in protected workspace
		{
			url += "/" + playgroundUUID;

			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "PUT", "test", 415);
			RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "PUT", null, playgroundValid2Json, MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", playgroundValid2Json, MediaType.APPLICATION_JSON, 200, false);
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

		//Check all non-playground-specific endpoints in private workspace
		{
			urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds";

			url = urlTemplate;
			RequestHelper.emitRequest(url, "GET", null, 401);
			RequestHelper.emitRequest(url, "GET", "test", 200);
			RequestHelper.emitRequest(url, "POST", "test", 415);
			RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		}

		//Create valid playground in private workspace
		{
			RequestHelper.emitRequest(url, "POST", null, playgroundValid1Json, MediaType.APPLICATION_JSON, 401, true);
			playgroundUUID = RequestHelper.emitRequest(url, "POST", "test", playgroundValid1Json, MediaType.APPLICATION_JSON, 201, true);
		}

		//Check all playground-specific endpoints in private workspace
		{
			url += "/" + playgroundUUID;

			RequestHelper.emitRequest(url, "GET", null, 401);
			RequestHelper.emitRequest(url, "GET", "test", 200);
			RequestHelper.emitRequest(url, "PUT", "test", 415);
			RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "PUT", null, playgroundValid2Json, MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", playgroundValid2Json, MediaType.APPLICATION_JSON, 200, false);
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
		Assertions.assertTrue(null != workspaceUUID && workspaceUUID.length() > 0);

		String url;

		Playground playground;
		String playgroundUUID;

		//Create playground
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds/";

			String playgroundJson = FileHelper.readFile("playground-test-1", "playground_1.json");
			playground = (Playground) SerializationHelper.getInstance().toClass(playgroundJson, Playground.class);
			playgroundUUID = RequestHelper.emitRequest(url, "POST", null, playgroundJson, MediaType.APPLICATION_JSON, 201, true);
		}

		//Get playground directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds/" + playgroundUUID;

			//Compare to original playground object
			String playgroundJson = RequestHelper.emitRequest(url, "GET", null, 200, false);
			Playground playgroundToCompare = (Playground) SerializationHelper.getInstance().toClass(playgroundJson, Playground.class);
			Assertions.assertEquals(playground.getName(), playgroundToCompare.getName());
			Assertions.assertEquals(playground.getDescription(), playgroundToCompare.getDescription());
			Assertions.assertEquals(playground.getExpression(), playgroundToCompare.getExpression());
			Assertions.assertEquals(playground.getContext(), playgroundToCompare.getContext());
		}

		//Get playground indirectly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds/";

			String playgroundJson = RequestHelper.emitRequest(url, "GET", null, 200, false);
			HashMap<String, Playground> playgrounds = SerializationHelper.getInstance().toClass(playgroundJson, new TypeReference<HashMap<String, Playground>>() {});

			Assertions.assertEquals(1, playgrounds.size());

			//Compare to original playground object
			Playground playgroundToCompare = playgrounds.get(playgroundUUID);
			Assertions.assertEquals(playground.getName(), playgroundToCompare.getName());
			Assertions.assertEquals(playground.getDescription(), playgroundToCompare.getDescription());
			Assertions.assertEquals(playground.getExpression(), playgroundToCompare.getExpression());
			Assertions.assertEquals(playground.getContext(), playgroundToCompare.getContext());
		}

		//Edit playground
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds/" + playgroundUUID;

			String playgroundEditJson = FileHelper.readFile("playground-test-1", "playground_1_edit.json");
			playground = (Playground) SerializationHelper.getInstance().toClass(playgroundEditJson, Playground.class);
			RequestHelper.emitRequest(url, "PUT", null, playgroundEditJson, MediaType.APPLICATION_JSON, 200, true);
		}

		//Get edited playground directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds/" + playgroundUUID;

			String playgroundEditJson = RequestHelper.emitRequest(url, "GET", null, 200, true);

			//Compare to original playground object
			Playground playgroundToCompare = (Playground) SerializationHelper.getInstance().toClass(playgroundEditJson, Playground.class);
			Assertions.assertEquals(playground.getName(), playgroundToCompare.getName());
			Assertions.assertEquals(playground.getDescription(), playgroundToCompare.getDescription());
			Assertions.assertEquals(playground.getExpression(), playgroundToCompare.getExpression());
			Assertions.assertEquals(playground.getContext(), playgroundToCompare.getContext());
		}

		//Get edited playground indirectly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds/";

			String playgroundJson = RequestHelper.emitRequest(url, "GET", null, 200, false);
			HashMap<String, Playground> playgrounds = SerializationHelper.getInstance().toClass(playgroundJson, new TypeReference<HashMap<String, Playground>>() {});

			Assertions.assertEquals(1, playgrounds.size());

			//Compare to original playground object
			Playground playgroundToCompare = playgrounds.get(playgroundUUID);
			Assertions.assertEquals(playground.getName(), playgroundToCompare.getName());
			Assertions.assertEquals(playground.getDescription(), playgroundToCompare.getDescription());
			Assertions.assertEquals(playground.getExpression(), playgroundToCompare.getExpression());
			Assertions.assertEquals(playground.getContext(), playgroundToCompare.getContext());
		}

		//Delete playground
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds/" + playgroundUUID;

			RequestHelper.emitRequest(url, "DELETE", null, 204);
		}

		//Get playground directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds/" + playgroundUUID;

			RequestHelper.emitRequest(url, "GET", null, 404);
		}

		//Get playground indirectly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/playgrounds/";

			String playgroundsJson = RequestHelper.emitRequest(url, "GET", null, 200, false);
			HashMap<String, Playground> playgrounds = SerializationHelper.getInstance().toClass(playgroundsJson, new TypeReference<HashMap<String, Playground>>() {});

			//Make sure the playground was removed from the overall pool
			Assertions.assertTrue(0 == playgrounds.size() && null == playgrounds.get(playgroundUUID));
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
