package de.materna.dmn.tester.servlets.workspace.beans;

import de.materna.dmn.tester.persistence.PersistenceDirectoryManager;
import de.materna.dmn.tester.persistence.PersistenceFileManager;
import de.materna.dmn.tester.servlets.input.beans.PersistedInput;
import de.materna.dmn.tester.servlets.output.beans.PersistedOutput;
import de.materna.dmn.tester.servlets.test.beans.PersistedTest;
import de.materna.jdec.DecisionSession;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Workspace {
	private static final Logger log = Logger.getLogger(Workspace.class);

	private PersistenceDirectoryManager<String> modelManager;

	private PersistenceDirectoryManager<PersistedInput> inputManager;
	private PersistenceDirectoryManager<PersistedOutput> outputManager;
	private PersistenceDirectoryManager<PersistedTest> testManager;

	private Configuration configuration;
	private AccessLog accessLog;

	private DecisionSession decisionSession;

	public Workspace(String workspaceUUID) throws IOException {
		modelManager = new PersistenceDirectoryManager<>(workspaceUUID, "models", String.class, "dmn");

		inputManager = new PersistenceDirectoryManager<>(workspaceUUID, "inputs", PersistedInput.class, "json");
		outputManager = new PersistenceDirectoryManager<>(workspaceUUID, "outputs", PersistedOutput.class, "json");
		testManager = new PersistenceDirectoryManager<>(workspaceUUID, "tests", PersistedTest.class, "json");

		PersistenceFileManager configurationManager = new PersistenceFileManager(workspaceUUID, "configuration.json");
		configuration = new Configuration(configurationManager);

		PersistenceFileManager accessLogManager = new PersistenceFileManager(workspaceUUID, "access.log");
		accessLog = new AccessLog(accessLogManager);

		decisionSession = new DecisionSession();
	}

	public PersistenceDirectoryManager getModelManager() {
		return modelManager;
	}

	public PersistenceDirectoryManager<PersistedInput> getInputManager() {
		return inputManager;
	}

	public PersistenceDirectoryManager<PersistedOutput> getOutputManager() {
		return outputManager;
	}

	public PersistenceDirectoryManager<PersistedTest> getTestManager() {
		return testManager;
	}

	public DecisionSession getDecisionSession() {
		return decisionSession;
	}

	public Configuration getConfig() {
		return configuration;
	}

	public AccessLog getAccessLog() {
		return accessLog;
	}
}
