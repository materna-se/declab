package de.materna.dmn.tester.servlets;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
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
import de.materna.dmn.tester.servlets.challenges.beans.Challenge;
import de.materna.dmn.tester.servlets.challenges.beans.Scenario;
import de.materna.jdec.serialization.SerializationHelper;

public class ChallengeServletTest {
	static String declabHost = ManagementHelper.declabHost;
	static String workspaceUUID = "";
	
	@BeforeEach
	public void createWorkspace() throws URISyntaxException, IOException {
		String createdWorkspaceUUID = RequestHelper.emitRequest(declabHost + "/api/workspaces", "POST", null, FileHelper.readFile("workspace_public.json"), MediaType.APPLICATION_JSON, 200, true);
		Assertions.assertTrue(createdWorkspaceUUID != null && createdWorkspaceUUID.length() > 0);

		workspaceUUID = createdWorkspaceUUID;
	}
	
	public void compareScenarios(List<Scenario> a, List<Scenario> b) {
		Assertions.assertEquals(a.size(), b.size());
		
		Iterator<Scenario> aIterator = a.iterator();
		Iterator<Scenario> bIterator = b.iterator();
		
		while(aIterator.hasNext()) {
			Scenario aS = aIterator.next();
			Scenario bS = bIterator.next();
			
			Assertions.assertEquals(aS.getName(), bS.getName());
			Assertions.assertEquals(aS.getInput().getValue(), bS.getInput().getValue());
			Assertions.assertEquals(aS.getOutput().getValue(), bS.getOutput().getValue());
		}
	}
	
	@Test
	public void verifyResponseStatuses() throws URISyntaxException, IOException {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);
		
		String urlTemplate;
		String url;
		
		String challengeUUID;
		
		String challengeValid1Json = FileHelper.readFile("challenge-test-1", "challenge_valid_1.json");
		String challengeValid2Json = FileHelper.readFile("challenge-test-1", "challenge_valid_2.json");
		
		//Check for non-existent challenges, make sure all endpoints handle as expected
		{
			String randomUUID = UUID.randomUUID().toString();
			urlTemplate = declabHost + "/api/workspaces/" + randomUUID + "/challenges";
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
		
		//Check all non-challenge-specific endpoints in public workspace
		{
			urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/challenges";
			url = urlTemplate;
			
			RequestHelper.emitRequest(url, "GET", null, 200);
			
			RequestHelper.emitRequest(url, "POST", null, 415);
			RequestHelper.emitRequest(url, "POST", null, "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 400, false);
		}
		
		//Try creating invalid challenges in public workspace
		{
			String challengeInvalid1Json = FileHelper.readFile("challenge-test-1", "challenge_invalid_1.json");
			RequestHelper.emitRequest(url, "POST", null, challengeInvalid1Json, MediaType.APPLICATION_JSON, 400, false);
			
			String challengeInvalid2Json = FileHelper.readFile("challenge-test-1", "challenge_invalid_2.json");
			RequestHelper.emitRequest(url, "POST", null, challengeInvalid2Json, MediaType.APPLICATION_JSON, 400, false);
			
			String challengeInvalid3Json = FileHelper.readFile("challenge-test-1", "challenge_invalid_3.json");
			RequestHelper.emitRequest(url, "POST", null, challengeInvalid3Json, MediaType.APPLICATION_JSON, 400, false);
		}
		
		//Create valid challenge in public workspace
		{
			challengeUUID = RequestHelper.emitRequest(url, "POST", null, challengeValid1Json, MediaType.APPLICATION_JSON, 201, true);
		}
		
		//Check all output-specific endpoints in public workspace
		{
			url += "/" + challengeUUID;
			RequestHelper.emitRequest(url, "GET", null, 200);
			
			RequestHelper.emitRequest(url, "PUT", null, 415);
			RequestHelper.emitRequest(url, "PUT", null, "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 400, false);
			
			RequestHelper.emitRequest(url, "PUT", null, challengeValid2Json, MediaType.APPLICATION_JSON, 204, false);
			
			RequestHelper.emitRequest(url, "DELETE", null, 204);
		}
		
		//Make workspace protected
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
			String protectedWorkspaceJson = FileHelper.readFile("workspace_protected.json");
			RequestHelper.emitRequest(url, "POST", null, protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
			RequestHelper.emitRequest(url, "POST", "test", protectedWorkspaceJson, MediaType.APPLICATION_JSON, 200, true);
		}
		
		//Check all non-challenge-specific endpoints in protected workspace
		{
			urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/challenges";
			url = urlTemplate;
			
			RequestHelper.emitRequest(url, "GET", null, 200);
			
			RequestHelper.emitRequest(url, "POST", "test", 415);
			RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		}
		
		//Create valid challenge in protected workspace
		{
			RequestHelper.emitRequest(url, "POST", null, challengeValid1Json, MediaType.APPLICATION_JSON, 401, true);
			challengeUUID = RequestHelper.emitRequest(url, "POST", "test", challengeValid1Json, MediaType.APPLICATION_JSON, 201, true);
		}
		
		//Check all output-specific endpoints
		{
			url += "/" + challengeUUID;
			
			RequestHelper.emitRequest(url, "GET", null, 200);
			
			RequestHelper.emitRequest(url, "PUT", "test", 415);
			RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "PUT", null, challengeValid2Json, MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", challengeValid2Json, MediaType.APPLICATION_JSON, 204, false);
			
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
		
		//Check all non-challenge-specific endpoints in private workspace
		{
			urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID + "/challenges";
			url = urlTemplate;
			
			RequestHelper.emitRequest(url, "GET", null, 401);
			RequestHelper.emitRequest(url, "GET", "test", 200);
			
			RequestHelper.emitRequest(url, "POST", "test", 415);
			RequestHelper.emitRequest(url, "POST", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "POST", "test", "", MediaType.APPLICATION_JSON, 400, false);
		}
		
		//Create valid challenge in private workspace
		{
			RequestHelper.emitRequest(url, "POST", null, challengeValid1Json, MediaType.APPLICATION_JSON, 401, true);
			challengeUUID = RequestHelper.emitRequest(url, "POST", "test", challengeValid1Json, MediaType.APPLICATION_JSON, 201, true);
		}
		
		//Check all challenge-specific endpoints in private workspace
		{
			url += "/" + challengeUUID;
			
			RequestHelper.emitRequest(url, "GET", null, 401);
			RequestHelper.emitRequest(url, "GET", "test", 200);
	
			RequestHelper.emitRequest(url, "PUT", "test", 415);
			RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.TEXT_PLAIN, 415, false);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.APPLICATION_JSON, 400, false);
			RequestHelper.emitRequest(url, "PUT", null, challengeValid2Json, MediaType.APPLICATION_JSON, 401, false);
			RequestHelper.emitRequest(url, "PUT", "test", challengeValid2Json, MediaType.APPLICATION_JSON, 204, false);
			
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
		
		Challenge challenge;
		String challengeUUID;

		//Create challenge
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/challenges/";
			
			String challengeJson = FileHelper.readFile("challenge-test-1", "challenge_valid_1.json");
			challenge = (Challenge) SerializationHelper.getInstance().toClass(challengeJson, Challenge.class);
			challengeUUID = RequestHelper.emitRequest(url, "POST", null, challengeJson, MediaType.APPLICATION_JSON, 201, true);
		}
		
		//Get challenge indirectly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/challenges/";
			
			String challengesJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			HashMap<String, Challenge> challenges = SerializationHelper.getInstance().toClass(challengesJson, new TypeReference<HashMap<String, Challenge>>() {});
			
			Assertions.assertEquals(1, challenges.size());
			
			Challenge challengeToCompare = challenges.get(challengeUUID);
			
			//Compare to the original challenge object
			Assertions.assertEquals(challenge.getName(), challengeToCompare.getName());
			Assertions.assertEquals(challenge.getDescription(), challengeToCompare.getDescription());
			Assertions.assertEquals(challenge.getHints(), challengeToCompare.getHints());
			Assertions.assertEquals(challenge.getSolution(), challengeToCompare.getSolution());
			compareScenarios(challenge.getScenarios(), challengeToCompare.getScenarios());
		}
		
		//Get challenge directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/challenges/" + challengeUUID;
			
			String challengeJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			
			Challenge challengeToCompare = (Challenge) SerializationHelper.getInstance().toClass(challengeJson, Challenge.class);
			
			//Compare to the original challenge object
			Assertions.assertEquals(challenge.getName(), challengeToCompare.getName());
			Assertions.assertEquals(challenge.getDescription(), challengeToCompare.getDescription());
			Assertions.assertEquals(challenge.getHints(), challengeToCompare.getHints());
			Assertions.assertEquals(challenge.getSolution(), challengeToCompare.getSolution());
			compareScenarios(challenge.getScenarios(), challengeToCompare.getScenarios());
		}
		
		//Edit challenge
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/challenges/" + challengeUUID;
			
			String challengeJson = FileHelper.readFile("challenge-test-1", "challenge_valid_2.json");
			challenge = (Challenge) SerializationHelper.getInstance().toClass(challengeJson, Challenge.class);
			RequestHelper.emitRequest(url, "PUT", null, challengeJson, MediaType.APPLICATION_JSON, 204, false);
		}
		
		//Get edited challenge indirectly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/challenges/";
			
			String challengesJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			HashMap<String, Challenge> challenges = SerializationHelper.getInstance().toClass(challengesJson, new TypeReference<HashMap<String, Challenge>>() {});
			
			Assertions.assertEquals(1, challenges.size());
			
			Challenge challengeToCompare = challenges.get(challengeUUID);
			
			//Compare to the original challenge object
			Assertions.assertEquals(challenge.getName(), challengeToCompare.getName());
			Assertions.assertEquals(challenge.getDescription(), challengeToCompare.getDescription());
			Assertions.assertEquals(challenge.getHints(), challengeToCompare.getHints());
			Assertions.assertEquals(challenge.getSolution(), challengeToCompare.getSolution());
			compareScenarios(challenge.getScenarios(), challengeToCompare.getScenarios());
		}
		
		//Get edited challenge directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/challenges/" + challengeUUID;
			
			String challengeJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			
			Challenge challengeToCompare = (Challenge) SerializationHelper.getInstance().toClass(challengeJson, Challenge.class);
			
			//Compare to the original challenge object
			Assertions.assertEquals(challenge.getName(), challengeToCompare.getName());
			Assertions.assertEquals(challenge.getDescription(), challengeToCompare.getDescription());
			Assertions.assertEquals(challenge.getHints(), challengeToCompare.getHints());
			Assertions.assertEquals(challenge.getSolution(), challengeToCompare.getSolution());
			compareScenarios(challenge.getScenarios(), challengeToCompare.getScenarios());
		}
		
		//Delete challenge
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/challenges/" + challengeUUID;
			
			RequestHelper.emitRequest(url, "DELETE", null, 204);
		}
		
		//Get challenges
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/challenges/";
			
			String challengesJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			HashMap<String, Challenge> challenges = SerializationHelper.getInstance().toClass(challengesJson, new TypeReference<HashMap<String, Challenge>>() {});
			
			Assertions.assertEquals(0, challenges.size());
		}
		
		//Get deleted challenge directly
		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/challenges/" + challengeUUID;
			
			RequestHelper.emitRequest(url, "GET", null, 404);
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
