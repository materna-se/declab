package de.materna.dmn.tester.servlets.workspace.beans;

import de.materna.dmn.tester.drools.helpers.DroolsHelper;
import de.materna.dmn.tester.persistence.PersistenceDirectoryManager;
import de.materna.dmn.tester.persistence.PersistenceFileManager;
import de.materna.dmn.tester.servlets.input.beans.PersistedInput;
import de.materna.dmn.tester.servlets.output.beans.PersistedOutput;
import de.materna.dmn.tester.servlets.playground.beans.Playground;
import de.materna.dmn.tester.servlets.playground.beans.Playground;
import de.materna.dmn.tester.servlets.test.beans.PersistedTest;
import de.materna.jdec.DMNDecisionSession;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Workspace {
	private static final Logger log = Logger.getLogger(Workspace.class);

	private PersistenceDirectoryManager<String> modelManager;
	private PersistenceDirectoryManager<Playground> playgroundManager;

	private PersistenceDirectoryManager<PersistedInput> inputManager;
	private PersistenceDirectoryManager<PersistedOutput> outputManager;
	private PersistenceDirectoryManager<PersistedTest> testManager;

	private Configuration configuration;
	private AccessLog accessLog;

	private DMNDecisionSession decisionSession;

	public Workspace(String workspaceUUID) throws IOException {
		modelManager = new PersistenceDirectoryManager<>(workspaceUUID, "models", String.class, "dmn");
		playgroundManager = new PersistenceDirectoryManager<>(workspaceUUID, "playgrounds", Playground.class, "json");

		inputManager = new PersistenceDirectoryManager<>(workspaceUUID, "inputs", PersistedInput.class, "json");
		outputManager = new PersistenceDirectoryManager<>(workspaceUUID, "outputs", PersistedOutput.class, "json");
		testManager = new PersistenceDirectoryManager<>(workspaceUUID, "tests", PersistedTest.class, "json");

		PersistenceFileManager configurationManager = new PersistenceFileManager(workspaceUUID, "configuration.json");
		configuration = new Configuration(configurationManager);

		PersistenceFileManager accessLogManager = new PersistenceFileManager(workspaceUUID, "access.log");
		accessLog = new AccessLog(accessLogManager);

		decisionSession = new DMNDecisionSession();

		DroolsHelper.initModels(this);
	}

	public PersistenceDirectoryManager<String> getModelManager() {
		return modelManager;
	}

	public PersistenceDirectoryManager<Playground> getPlaygroundManager() {
		return playgroundManager;
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

	public DMNDecisionSession getDecisionSession() {
		return decisionSession;
	}

	public void clearDecisionSession() {
		decisionSession.close();
		decisionSession = new DMNDecisionSession();
	}

	public Configuration getConfig() {
		return configuration;
	}

	public AccessLog getAccessLog() {
		return accessLog;
	}
}
