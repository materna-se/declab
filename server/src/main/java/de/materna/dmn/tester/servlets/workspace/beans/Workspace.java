package de.materna.dmn.tester.servlets.workspace.beans;

import de.materna.dmn.tester.helpers.HashingHelper;
import de.materna.dmn.tester.persistence.PersistenceDirectoryManager;
import de.materna.dmn.tester.persistence.PersistenceFileManager;
import de.materna.dmn.tester.servlets.input.beans.PersistedInput;
import de.materna.dmn.tester.servlets.output.beans.PersistedOutput;
import de.materna.dmn.tester.servlets.test.beans.PersistedTest;
import de.materna.dmn.tester.servlets.workspace.beans.PublicConfiguration.Access;
import de.materna.jdec.DecisionSession;
import de.materna.jdec.model.ModelImportException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Workspace {
	private static final Logger log = Logger.getLogger(Workspace.class);

	private PersistenceFileManager modelManager;

	private PersistenceDirectoryManager<PersistedInput> inputManager;
	private PersistenceDirectoryManager<PersistedOutput> outputManager;
	private PersistenceDirectoryManager<PersistedTest> testManager;

	private Configuration configuration;
	private AccessLog accessLog;

	private DecisionSession decisionSession;

	public Workspace(String workspaceUUID) throws IOException {
		modelManager = new PersistenceFileManager(workspaceUUID, "model.dmn");

		inputManager = new PersistenceDirectoryManager<>(workspaceUUID, "inputs", PersistedInput.class);
		outputManager = new PersistenceDirectoryManager<>(workspaceUUID, "outputs", PersistedOutput.class);
		testManager = new PersistenceDirectoryManager<>(workspaceUUID, "tests", PersistedTest.class);

		PersistenceFileManager configurationManager = new PersistenceFileManager(workspaceUUID, "configuration.json");
		configuration = new Configuration(configurationManager);
		PersistenceFileManager accessLogManager = new PersistenceFileManager(workspaceUUID, "access.log");
		accessLog = new AccessLog(accessLogManager);

		decisionSession = new DecisionSession();

		if (!configurationManager.fileExists()) {
			// If the configuration file doesn't exist yet, we'll create it with default values.
			configuration.setVersion(1);
			configuration.setName(workspaceUUID);
			configuration.setAccess(Access.PUBLIC);
			configuration.setCreatedDate(System.currentTimeMillis());
			configuration.setModifiedDate(configuration.getCreatedDate());

			try {
				byte[] saltBytes = new byte[64];
				SecureRandom.getInstanceStrong().nextBytes(saltBytes);
				configuration.setSalt(HashingHelper.getInstance().byteArrayToHexString(saltBytes));
			} catch (NoSuchAlgorithmException e) {
				log.warn("Could not instantiate SecureRandom, salt is null");
			}

			configuration.serialize();
		}
		else {
			// If the configuration file exists, we'll open it.
			configuration.fromJson(configurationManager.getFile());
		}

		try {
			decisionSession.importModel("main", "main", modelManager.getFile());
		}
		catch (IOException | ModelImportException e) {
			log.warn("No valid model was found, import process is stopped.");
		}
	}

	public PersistenceFileManager getModelManager() {
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
