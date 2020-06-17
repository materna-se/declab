package de.materna.dmn.tester.servlets;

import com.fasterxml.jackson.core.type.TypeReference;
import de.materna.dmn.tester.FileHelper;
import de.materna.dmn.tester.ManagementHelper;
import de.materna.dmn.tester.RequestHelper;
import de.materna.dmn.tester.TestHelper;
import de.materna.dmn.tester.servlets.workspace.beans.Configuration;
import de.materna.jdec.model.Model;
import de.materna.jdec.serialization.SerializationHelper;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class WorkspaceServletTest {
	static String declabHost = ManagementHelper.declabHost;
	static int configurationVersion = ManagementHelper.configurationVersion;

	static LinkedList<String> workspaceUUIDs = new LinkedList<>();
	static String workspaceUUID = "";

	@AfterAll
	public static void deleteOrphanedWorkspaces() {
		for (String uuid : workspaceUUIDs) {
			RequestHelper.emitRequest(declabHost + "/api/workspaces/" + uuid, "DELETE", "test", 204);
		}
	}

	@BeforeEach
	public void createWorkspace() throws URISyntaxException, IOException {
		//Create workspace
		String createdWorkspaceUUID = RequestHelper.emitRequest(declabHost + "/api/workspaces", "POST", null, FileHelper.readFile("workspace_public.json"), MediaType.APPLICATION_JSON, 200, true);
		Assertions.assertTrue(createdWorkspaceUUID != null && createdWorkspaceUUID.length() > 0);

		workspaceUUID = createdWorkspaceUUID;
		workspaceUUIDs.add(workspaceUUID);
	}

	@Test
	public void verifyResponseStatuses() throws IOException, URISyntaxException {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);

		String url;
		String urlTemplate;

		//Check for non-existent workspaces, make sure all endpoints handle as expected
		{
			urlTemplate = declabHost + "/api/workspaces/" + UUID.randomUUID().toString();

			url = urlTemplate + "/public";
			RequestHelper.emitRequest(url, "GET", null, 404);

			url = urlTemplate + "/config";
			RequestHelper.emitRequest(url, "GET", null, 404);

			RequestHelper.emitRequest(url, "POST", null, 415);
			RequestHelper.emitRequest(url, "POST", null, "", MediaType.APPLICATION_JSON, 404, false);

			url = urlTemplate + "/log";
			RequestHelper.emitRequest(url, "GET", null, 404);

			url = urlTemplate + "/backup";
			RequestHelper.emitRequest(url, "GET", null, 404);
			RequestHelper.emitRequest(url, "PUT", null, 415);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.MULTIPART_FORM_DATA, 404, false);

			url = urlTemplate;
			RequestHelper.emitRequest(url, "DELETE", null, 404);
		}

		//Create public workspace, make sure all endpoints handle as expected
		{
			url = declabHost + "/api/workspaces/";
			String publicWorkspaceUUID = RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("workspace_public.json"), MediaType.APPLICATION_JSON, 200, true);

			workspaceUUIDs.add(publicWorkspaceUUID);

			urlTemplate = declabHost + "/api/workspaces/" + publicWorkspaceUUID;

			url = urlTemplate + "/public";
			RequestHelper.emitRequest(url, "GET", null, 200);

			url = urlTemplate + "/config";
			RequestHelper.emitRequest(url, "GET", null, 200);

			url = urlTemplate + "/log";
			RequestHelper.emitRequest(url, "GET", null, 200);

			url = urlTemplate + "/backup";
			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "PUT", null, "{}", MediaType.APPLICATION_JSON, 415, false);
			RequestHelper.emitRequest(url, "PUT", null, "", MediaType.MULTIPART_FORM_DATA, 503, false);

			url = urlTemplate;
			RequestHelper.emitRequest(url, "DELETE", null, 204);

			workspaceUUIDs.remove(publicWorkspaceUUID);
		}

		//Create protected workspace, make sure all endpoints handle as expected
		{
			url = declabHost + "/api/workspaces/";
			String protectedWorkspaceUUID = RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("workspace_protected.json"), MediaType.APPLICATION_JSON, 200, true);

			workspaceUUIDs.add(protectedWorkspaceUUID);

			urlTemplate = declabHost + "/api/workspaces/" + protectedWorkspaceUUID;

			url = urlTemplate + "/public";
			RequestHelper.emitRequest(url, "GET", null, 200);

			url = urlTemplate + "/config";
			RequestHelper.emitRequest(url, "GET", null, 401);
			RequestHelper.emitRequest(url, "GET", "test", 200);

			url = urlTemplate + "/log";
			RequestHelper.emitRequest(url, "GET", null, 401);
			RequestHelper.emitRequest(url, "GET", "test", 200);

			url = urlTemplate + "/backup";
			RequestHelper.emitRequest(url, "GET", null, 200);
			RequestHelper.emitRequest(url, "PUT", null, 415);
			RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.APPLICATION_JSON, 415, false);
			RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.MULTIPART_FORM_DATA, 503, false);

			url = urlTemplate;
			RequestHelper.emitRequest(url, "DELETE", null, 401);
			RequestHelper.emitRequest(url, "DELETE", "test", 204);

			workspaceUUIDs.remove(protectedWorkspaceUUID);
		}

		//Create private workspace, make sure all endpoints handle as expected
		{
			url = declabHost + "/api/workspaces/";
			String privateWorkspaceUUID = RequestHelper.emitRequest(url, "POST", null, FileHelper.readFile("workspace_private.json"), MediaType.APPLICATION_JSON, 200, true);

			workspaceUUIDs.add(privateWorkspaceUUID);

			urlTemplate = declabHost + "/api/workspaces/" + privateWorkspaceUUID;

			url = urlTemplate + "/public";
			RequestHelper.emitRequest(url, "GET", null, 200);

			url = urlTemplate + "/config";
			RequestHelper.emitRequest(url, "GET", null, 401);
			RequestHelper.emitRequest(url, "GET", "test", 200);

			url = urlTemplate + "/log";
			RequestHelper.emitRequest(url, "GET", null, 401);
			RequestHelper.emitRequest(url, "GET", "test", 200);

			url = urlTemplate + "/backup";
			RequestHelper.emitRequest(url, "GET", null, 401);
			RequestHelper.emitRequest(url, "GET", "test", 200);
			RequestHelper.emitRequest(url, "PUT", null, 415);
			RequestHelper.emitRequest(url, "PUT", "test", "{}", MediaType.APPLICATION_JSON, 415, false);
			RequestHelper.emitRequest(url, "PUT", "test", "", MediaType.MULTIPART_FORM_DATA, 503, false);

			url = urlTemplate;
			RequestHelper.emitRequest(url, "DELETE", null, 401);
			RequestHelper.emitRequest(url, "DELETE", "test", 204);

			workspaceUUIDs.remove(privateWorkspaceUUID);
		}
	}

	@Test
	public void verifyWorkspaces() {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);

		String url;

		{
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/public";

			//Get public workspace configuration
			String publicConfigJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			HashMap<String, ?> publicConfigMap = SerializationHelper.getInstance().toClass(publicConfigJson, new TypeReference<HashMap<String, ?>>() {
			});
			Assertions.assertEquals(configurationVersion, publicConfigMap.get("version"));
			Assertions.assertEquals("Workspace", publicConfigMap.get("name"));
			Assertions.assertEquals("Description", publicConfigMap.get("description"));
			Assertions.assertEquals("PUBLIC", publicConfigMap.get("access"));
			Assertions.assertEquals(4, publicConfigMap.size());
		}

		{
			//Get full workspace configuration
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";

			String fullConfigJson = RequestHelper.emitRequest(url, "GET", null, 200, true);
			HashMap<String, ?> fullConfigMap = SerializationHelper.getInstance().toClass(fullConfigJson, new TypeReference<HashMap<String, ?>>() {
			});
			Assertions.assertEquals(configurationVersion, fullConfigMap.get("version"));
			Assertions.assertEquals("Workspace", fullConfigMap.get("name"));
			Assertions.assertEquals("Description", fullConfigMap.get("description"));
			Assertions.assertEquals("PUBLIC", fullConfigMap.get("access"));

			Assertions.assertNotEquals(null, fullConfigMap.get("token"));
			Assertions.assertNotEquals("", fullConfigMap.get("token"));

			Assertions.assertNotEquals(null, fullConfigMap.get("salt"));
			Assertions.assertNotEquals("", fullConfigMap.get("salt"));

			Assertions.assertNotEquals(null, fullConfigMap.get("createdDate"));
			Assertions.assertNotEquals(0, fullConfigMap.get("createdDate"));

			Assertions.assertNotEquals(null, fullConfigMap.get("modifiedDate"));
			Assertions.assertNotEquals(0, fullConfigMap.get("modifiedDate"));

			Assertions.assertEquals(new ArrayList<String>(), fullConfigMap.get("models"));

			{
				//Edit name and description
				String workspaceEdit1Json = "{\"name\":\"edit\",\"description\":\"edit\"}";
				RequestHelper.emitRequest(url, "POST", null, workspaceEdit1Json, MediaType.APPLICATION_JSON, 200, false);

				//Get edited public workspace configuration #1
				url = declabHost + "/api/workspaces/" + workspaceUUID + "/public";

				String publicConfigEdit1Json = RequestHelper.emitRequest(url, "GET", null, 200, true);
				HashMap<String, ?> publicConfigEdit1Map = SerializationHelper.getInstance().toClass(publicConfigEdit1Json, new TypeReference<HashMap<String, ?>>() {
				});
				Assertions.assertEquals(configurationVersion, publicConfigEdit1Map.get("version"));
				Assertions.assertEquals("edit", publicConfigEdit1Map.get("name"));
				Assertions.assertEquals("edit", publicConfigEdit1Map.get("description"));
				Assertions.assertEquals("PUBLIC", publicConfigEdit1Map.get("access"));

				//Get edited full workspace configuration #1
				url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";

				String fullConfigEdit1Json = RequestHelper.emitRequest(url, "GET", null, 200, true);
				HashMap<String, ?> fullConfigEdit1Map = SerializationHelper.getInstance().toClass(fullConfigEdit1Json, new TypeReference<HashMap<String, ?>>() {
				});
				Assertions.assertEquals(fullConfigMap.get("token"), fullConfigEdit1Map.get("token"));
				Assertions.assertEquals(fullConfigMap.get("salt"), fullConfigEdit1Map.get("salt"));
				Assertions.assertEquals(fullConfigMap.get("createdDate"), fullConfigEdit1Map.get("createdDate"));
				Assertions.assertNotEquals(fullConfigMap.get("modifiedDate"), fullConfigEdit1Map.get("modifiedDate"));
			}

			{
				//Edit access mode and token
				String workspaceEdit2Json = "{\"access\":\"PRIVATE\",\"token\":\"test_two\"}";

				RequestHelper.emitRequest(url, "POST", null, workspaceEdit2Json, MediaType.APPLICATION_JSON, 200, false);

				//Get public configuration again
				url = declabHost + "/api/workspaces/" + workspaceUUID + "/public";

				String publicConfigEdit2Json = RequestHelper.emitRequest(url, "GET", null, 200, true);
				HashMap<String, ?> publicConfigEdit2Map = SerializationHelper.getInstance().toClass(publicConfigEdit2Json, new TypeReference<HashMap<String, ?>>() {
				});
				Assertions.assertEquals(configurationVersion, publicConfigEdit2Map.get("version"));
				Assertions.assertEquals("edit", publicConfigEdit2Map.get("name"));
				Assertions.assertEquals("edit", publicConfigEdit2Map.get("description"));
				Assertions.assertEquals("PRIVATE", publicConfigEdit2Map.get("access"));

				//Get private configuration, check that unauthorized access does in fact fail
				url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";

				RequestHelper.emitRequest(url, "GET", null, 401);

				String token = "test_two";

				String fullConfigEdit2Json = RequestHelper.emitRequest(url, "GET", token, 200, true);
				HashMap<String, ?> fullConfigEdit2Map = SerializationHelper.getInstance().toClass(fullConfigEdit2Json, new TypeReference<HashMap<String, ?>>() {
				});
				Assertions.assertNotEquals(fullConfigMap.get("token"), fullConfigEdit2Map.get("token"));
				Assertions.assertEquals(fullConfigMap.get("salt"), fullConfigEdit2Map.get("salt"));
				Assertions.assertEquals(fullConfigMap.get("createdDate"), fullConfigEdit2Map.get("createdDate"));
				Assertions.assertNotEquals(fullConfigMap.get("modifiedDate"), fullConfigEdit2Map.get("modifiedDate"));
			}
		}

		{
			//Get workspaces right now
			url = declabHost + "/api/workspaces/";
			String getWorkspaces1Json = RequestHelper.emitRequest(url, "GET", null, 200, true);
			HashMap<String, ?> getWorkspaces1Map = SerializationHelper.getInstance().toClass(getWorkspaces1Json, new TypeReference<HashMap<String, ?>>() {
			});
			Assertions.assertTrue(getWorkspaces1Map.get(workspaceUUID) != null);

			//Create another workspace
			String workspace2Json = "{\"name\":\"Import Test\",\"description\":\"VERY UNUSUAL TEST, BE CAREFUL EVERYONE\",\"access\":\"PROTECTED\",\"token\":\"test\"}";
			String createdWorkspace2UUID = RequestHelper.emitRequest(url, "POST", null, workspace2Json, MediaType.APPLICATION_JSON, 200, true);

			workspaceUUIDs.add(createdWorkspace2UUID);

			Assertions.assertTrue(createdWorkspace2UUID != null && createdWorkspace2UUID.length() > 0);

			//Get workspaces after adding one more
			String getWorkspaces2Json = RequestHelper.emitRequest(url, "GET", null, 200, true);
			HashMap<String, ?> getWorkspaces2Map = SerializationHelper.getInstance().toClass(getWorkspaces2Json, new TypeReference<HashMap<String, ?>>() {
			});
			Assertions.assertTrue(getWorkspaces2Map.get(workspaceUUID) != null);
			Assertions.assertTrue(getWorkspaces2Map.get(createdWorkspace2UUID) != null);

			//Make sure the number of workspaces makes sense
			Assertions.assertEquals(getWorkspaces1Map.size() + 1, getWorkspaces2Map.size());

			//Delete extra workspace without token, fail
			url = declabHost + "/api/workspaces/" + createdWorkspace2UUID;
			RequestHelper.emitRequest(url, "DELETE", null, 401);

			//Get public config again
			url += "/public";
			RequestHelper.emitRequest(url, "GET", null, 200);

			//Get full config again, fail if unauthorized
			url = declabHost + "/api/workspaces/" + createdWorkspace2UUID + "/config";
			RequestHelper.emitRequest(url, "GET", null, 401);
			RequestHelper.emitRequest(url, "GET", "test", 200);

			//Delete with authorization this time
			url = declabHost + "/api/workspaces/" + createdWorkspace2UUID;
			RequestHelper.emitRequest(url, "DELETE", "test", 204);

			//Make sure workspace is actually deleted
			url += "/public";
			RequestHelper.emitRequest(url, "GET", null, 404);

			workspaceUUIDs.remove(createdWorkspace2UUID);

			//Get workspaces after deleting extra workspace
			url = declabHost + "/api/workspaces/";
			String getWorkspaces3Json = RequestHelper.emitRequest(url, "GET", null, 200, true);
			HashMap<String, ?> getWorkspaces3Map = SerializationHelper.getInstance().toClass(getWorkspaces3Json, new TypeReference<HashMap<String, ?>>() {
			});
			Assertions.assertTrue(getWorkspaces3Map.get(workspaceUUID) != null);
			Assertions.assertTrue(getWorkspaces3Map.get(createdWorkspace2UUID) == null);
			Assertions.assertTrue(getWorkspaces3Map.size() == getWorkspaces1Map.size());
		}

		{
			//Get access log of main workspace
			url += workspaceUUID + "/log";
			RequestHelper.emitRequest(url, "GET", null, 401);
			String accessLogJson = RequestHelper.emitRequest(url, "GET", "test_two", 200, true);
			LinkedList<HashMap<String, ?>> accessLogMap = SerializationHelper.getInstance().toClass(accessLogJson, new TypeReference<LinkedList<HashMap<String, ?>>>() {
			});
			Assertions.assertTrue(accessLogMap.size() > 0);
		}

		{
			//Make main workspace public to clean up
			String workspaceEdit3Json = "{\"access\":\"PUBLIC\",\"token\":\"test\"}";
			url = declabHost + "/api/workspaces/" + workspaceUUID + "/config";
			RequestHelper.emitRequest(url, "POST", "test_two", workspaceEdit3Json, MediaType.APPLICATION_JSON, 200, false);
		}
	}

	@Test
	public void verifyImportExport() throws IOException, URISyntaxException {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);

		String urlTemplate = declabHost + "/api/workspaces/" + workspaceUUID;
		String url = urlTemplate + "/config";

		//Get current workspace configuration
		Configuration configBefore = (Configuration) SerializationHelper.getInstance().toClass(RequestHelper.emitRequest(url, "GET", null, 200, true), Configuration.class);

		//Navigate to workspace .dtar
		Path rootPath = TestHelper.getRootPath();
		File workspaceDir = rootPath.resolve("scenarios").resolve("import-export-test-1").toFile();
		File workspaceDtar = new File(workspaceDir.getPath() + File.separator + "import-export-test-1.dtar");
		Assertions.assertTrue(workspaceDtar.exists() && workspaceDtar.isFile() && workspaceDtar.canRead());
		byte[] workspaceDtarBytes = Files.readAllBytes(workspaceDtar.toPath());

		{
			url = urlTemplate + "/backup";

			//Get known-good import configuration for comparison
			Configuration goldenConfig = (Configuration) SerializationHelper.getInstance().toClass(new String(Files.readAllBytes(Paths.get(workspaceDir.getAbsolutePath(), "configuration.json")), "UTF-8"), Configuration.class);

			//Import workspace
			RequestHelper.emitRequestMultipartFormData(url, "PUT", null, "backup", workspaceDtar, 204);

			//Get full configuration
			url = urlTemplate + "/config";
			Configuration configImported = (Configuration) SerializationHelper.getInstance().toClass(RequestHelper.emitRequest(url, "GET", null, 200, true), Configuration.class);

			//Compare configurations
			Assertions.assertEquals(goldenConfig.getVersion(), configImported.getVersion());
			Assertions.assertEquals(configBefore.getToken(), configImported.getToken());
			Assertions.assertEquals(configBefore.getSalt(), configImported.getSalt());
			Assertions.assertEquals(configBefore.getCreatedDate(), configImported.getCreatedDate());
			Assertions.assertNotEquals(configBefore.getModifiedDate(), configImported.getModifiedDate());
			Assertions.assertEquals(goldenConfig.getModels(), configImported.getModels());
		}

		//Check that the files are returned upon request
		{
			//TODO This is not particularly precise
			url = urlTemplate + "/inputs";
			HashMap<String, ?> inputs = SerializationHelper.getInstance().toClass(RequestHelper.emitRequest(url, "GET", null, 200, true), new TypeReference<HashMap<String, ?>>() {
			});
			Assertions.assertTrue(inputs.size() > 0);
		}

		{
			url = urlTemplate + "/model";
			List<Model> models = SerializationHelper.getInstance().toClass(RequestHelper.emitRequest(url, "GET", null, 200, true), new TypeReference<List<Model>>() {
			});
			Assertions.assertTrue(models.size() > 0);
		}

		{
			url = urlTemplate + "/outputs";
			HashMap<String, ?> outputs = SerializationHelper.getInstance().toClass(RequestHelper.emitRequest(url, "GET", null, 200, true), new TypeReference<HashMap<String, ?>>() {
			});
			Assertions.assertTrue(outputs.size() > 0);
		}

		{
			url = urlTemplate + "/playgrounds";
			HashMap<String, ?> playgrounds = SerializationHelper.getInstance().toClass(RequestHelper.emitRequest(url, "GET", null, 200, true), new TypeReference<HashMap<String, ?>>() {
			});
			Assertions.assertTrue(playgrounds.size() > 0);
		}

		{
			url = urlTemplate + "/tests";
			HashMap<String, ?> tests = SerializationHelper.getInstance().toClass(RequestHelper.emitRequest(url, "GET", null, 200, true), new TypeReference<HashMap<String, ?>>() {
			});
			Assertions.assertTrue(tests.size() > 0);
		}

		//Edit configuration to be practically identical to golden configuration
		url = urlTemplate + "/config";
		String configEdit1Json = "{\"name\":\"import-export-test-1\",\"description\":\"import-export-test-1-description\"}";
		RequestHelper.emitRequest(url, "POST", null, configEdit1Json, MediaType.APPLICATION_JSON, 200, true);

		//Export workspace
		url = urlTemplate + "/backup";
		byte[] workspaceExportDtarBytes = RequestHelper.emitRequestRawResponse(url, "GET", null, 200);

		//Create temporary directory for file comparison
		Path tempDirectory = Paths.get(workspaceDir.getAbsolutePath(), "__TEMP");
		if (!Files.exists(tempDirectory)) {
			Files.createDirectory(tempDirectory);
		}

		//Crawl exported .dtar, compare to golden .dtar
		Path goldenFilePath = Paths.get(tempDirectory.toString(), "golden.file");
		Path exportedFilePath = Paths.get(tempDirectory.toString(), "exported.file");

		InputStream goldenIS = new ByteArrayInputStream(workspaceDtarBytes);
		ZipInputStream goldenZIPIS = new ZipInputStream(goldenIS);

		InputStream exportedIS = new ByteArrayInputStream(workspaceExportDtarBytes);
		ZipInputStream exportedZIPIS = new ZipInputStream(exportedIS);

		while (true) {
			ZipEntry goldenZE = goldenZIPIS.getNextEntry();
			ZipEntry exportedZE = exportedZIPIS.getNextEntry();
			if (goldenZE == null || exportedZE == null) {
				Assertions.assertEquals(goldenZE, exportedZE);
				break;
			}

			//Write both files
			Files.write(goldenFilePath, IOUtils.toByteArray(goldenZIPIS));
			Files.write(exportedFilePath, IOUtils.toByteArray(exportedZIPIS));

			//Read both files
			String goldenFileContentString = new String(Files.readAllBytes(goldenFilePath), StandardCharsets.UTF_8);
			String exportedFileContentString = new String(Files.readAllBytes(exportedFilePath), StandardCharsets.UTF_8);

			Assertions.assertEquals(goldenFileContentString, exportedFileContentString);
		}

		Files.delete(goldenFilePath);
		Files.delete(exportedFilePath);
		Files.delete(tempDirectory);
	}

	@AfterEach
	public void deleteWorkspace() {
		Assertions.assertTrue(workspaceUUID != null && workspaceUUID.length() > 0);

		String url = declabHost + "/api/workspaces/" + workspaceUUID;
		RequestHelper.emitRequest(url, "DELETE", "test", 204);

		url += "/public";
		RequestHelper.emitRequest(url, "GET", null, 404);

		workspaceUUIDs.remove(workspaceUUID);
	}
}
