package de.materna.dmn.tester.persistence;

import de.materna.dmn.tester.TestHelper;
import de.materna.dmn.tester.servlets.input.beans.PersistedInput;
import de.materna.dmn.tester.servlets.output.beans.PersistedOutput;
import de.materna.dmn.tester.servlets.test.beans.PersistedTest;
import de.materna.dmn.tester.servlets.workspace.beans.Configuration;
import de.materna.dmn.tester.servlets.workspace.beans.PublicConfiguration;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.serialization.SerializationHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

class WorkspaceManagerTest {
	@Test
	void migrateWorkspace() throws IOException, URISyntaxException {
		TestHelper.applyScenario("migration-test-1");

		WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
		workspaceManager.indexAll();

		Map<String, Workspace> workspaces = workspaceManager.getAll();
		System.out.println(workspaces);
		Assertions.assertEquals(1, workspaces.size());

		Map.Entry<String, Workspace> workspaceEntry = workspaces.entrySet().iterator().next();
		// The workspace key should be an UUID.
		Assertions.assertEquals(36, workspaceEntry.getKey().length());

		Workspace workspace = workspaceEntry.getValue();

		Assertions.assertEquals(1, workspace.getModelManager().getFiles().size());

		workspace.getDecisionSession().getModel("https://github.com/kiegroup/kie-dmn");

		Assertions.assertEquals(1, workspace.getInputManager().getFiles().size());
		PersistedInput persistedInput = workspace.getInputManager().getFile("62758f4a-57c5-4970-a655-ff945b835a89");
		Assertions.assertEquals("Employment Status = EMPLOYED", persistedInput.getName());
		Assertions.assertEquals("{\"Employment Status\":\"EMPLOYED\"}", SerializationHelper.getInstance().toJSON(persistedInput.getValue()));

		Assertions.assertEquals(1, workspace.getOutputManager().getFiles().size());
		PersistedOutput persistedOutput = workspace.getOutputManager().getFile("ea49cc28-8bb6-49a4-93bd-019ce696f53d");
		Assertions.assertEquals("Employment Status = EMPLOYED", persistedOutput.getName());
		Assertions.assertEquals("Employment Status Statement", persistedOutput.getDecision());
		Assertions.assertEquals("\"You are EMPLOYED\"", SerializationHelper.getInstance().toJSON(persistedOutput.getValue()));

		Assertions.assertEquals(1, workspace.getTestManager().getFiles().size());
		PersistedTest persistedTest = workspace.getTestManager().getFile("bcc12dc9-ffc3-4026-9778-99549f10c5d2");
		Assertions.assertEquals("Employment Status = EMPLOYED", persistedTest.getName());
		Assertions.assertEquals("62758f4a-57c5-4970-a655-ff945b835a89", persistedTest.getInput());
		Assertions.assertEquals(1, persistedTest.getOutputs().size());
		Assertions.assertEquals("ea49cc28-8bb6-49a4-93bd-019ce696f53d", persistedTest.getOutputs().get(0));

		Configuration configuration = workspace.getConfig();
		Assertions.assertEquals("0003-input-data-string-allowed-values", configuration.getName());
		Assertions.assertEquals(PublicConfiguration.Access.PUBLIC, configuration.getAccess());
		Assertions.assertEquals(2, configuration.getVersion());
		Assertions.assertEquals(1, configuration.getModels().size());
		Map<String, String> configurationModel = configuration.getModels().get(0);
		Assertions.assertEquals("0003-input-data-string-allowed-values", configurationModel.get("name"));
		Assertions.assertEquals("https://github.com/kiegroup/kie-dmn", configurationModel.get("namespace"));
		Assertions.assertEquals(workspace.getModelManager().getFiles().keySet().iterator().next(), configurationModel.get("uuid"));
	}

	@Test
	void migrateWorkspaceWithInvalidModel() throws IOException, URISyntaxException {
		TestHelper.applyScenario("migration-test-2");

		// Should throw the log message "Could not index workspace 0003-input-data-string-allowed-values": ModelImportException
		WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
		workspaceManager.indexAll();
	}

	@Test
	void migrateWorkspaceWithInvalidEntity() throws IOException, URISyntaxException {
		TestHelper.applyScenario("migration-test-3");

		// Should throw the log message "Could not index workspace 0003-input-data-string-allowed-values": IOException
		WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
		workspaceManager.indexAll();
	}
}