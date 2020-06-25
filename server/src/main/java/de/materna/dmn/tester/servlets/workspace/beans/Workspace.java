package de.materna.dmn.tester.servlets.workspace.beans;

import de.materna.dmn.tester.drools.helpers.DroolsHelper;
import de.materna.dmn.tester.persistence.PersistenceDirectoryManager;
import de.materna.dmn.tester.persistence.PersistenceFileManager;
import de.materna.dmn.tester.servlets.challenges.beans.Challenge;
import de.materna.dmn.tester.servlets.input.beans.PersistedInput;
import de.materna.dmn.tester.servlets.output.beans.PersistedOutput;
import de.materna.dmn.tester.servlets.playground.beans.Playground;
import de.materna.dmn.tester.servlets.test.beans.PersistedTest;
import de.materna.jdec.HybridDecisionSession;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Workspace {
	private static final Logger log = Logger.getLogger(Workspace.class);

	private PersistenceDirectoryManager<Challenge> challengeManager;
	
	private PersistenceDirectoryManager<String> modelManager;
	private PersistenceDirectoryManager<Playground> playgroundManager;

	private PersistenceDirectoryManager<PersistedInput> inputManager;
	private PersistenceDirectoryManager<PersistedOutput> outputManager;
	private PersistenceDirectoryManager<PersistedTest> testManager;

	private PersistenceFileManager configurationManager;
	private Configuration configuration;
	private PersistenceFileManager accessLogManager;
	private AccessLog accessLog;

	private HybridDecisionSession decisionSession;

	public Workspace(String workspaceUUID) throws Exception {
		modelManager = new PersistenceDirectoryManager<>(workspaceUUID, "models", String.class, "dmn");
		playgroundManager = new PersistenceDirectoryManager<>(workspaceUUID, "playgrounds", Playground.class, "json");
		challengeManager = new PersistenceDirectoryManager<>(workspaceUUID, "challenges", Challenge.class, "json");
		inputManager = new PersistenceDirectoryManager<>(workspaceUUID, "inputs", PersistedInput.class, "json");
		outputManager = new PersistenceDirectoryManager<>(workspaceUUID, "outputs", PersistedOutput.class, "json");
		testManager = new PersistenceDirectoryManager<>(workspaceUUID, "tests", PersistedTest.class, "json");

		configurationManager = new PersistenceFileManager(workspaceUUID, "configuration.json");
		configuration = new Configuration(configurationManager);

		accessLogManager = new PersistenceFileManager(workspaceUUID, "access.log");
		accessLog = new AccessLog(accessLogManager);

		decisionSession = new HybridDecisionSession();

		DroolsHelper.importModels(this);
	}
	
	public PersistenceDirectoryManager<Challenge> getChallengeManager() {
		return challengeManager;
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

	public HybridDecisionSession getDecisionSession() {
		return decisionSession;
	}

	public void clearDecisionSession() throws Exception {
		decisionSession = new HybridDecisionSession();
	}

	public Configuration getConfig() {
		return configuration;
	}

	public AccessLog getAccessLog() {
		return accessLog;
	}

	public void verify() throws IOException {
		modelManager.verifyAllFiles();
		playgroundManager.verifyAllFiles();
		inputManager.verifyAllFiles();
		outputManager.verifyAllFiles();
		testManager.verifyAllFiles();
	}
}
