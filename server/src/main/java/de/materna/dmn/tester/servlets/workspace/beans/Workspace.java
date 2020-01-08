package de.materna.dmn.tester.servlets.workspace.beans;

import de.materna.dmn.tester.persistence.PersistenceDirectoryManager;
import de.materna.dmn.tester.persistence.PersistenceFileManager;
import de.materna.dmn.tester.servlets.input.beans.PersistedInput;
import de.materna.dmn.tester.servlets.output.beans.PersistedOutput;
import de.materna.dmn.tester.servlets.test.beans.PersistedTest;
import de.materna.jdec.DecisionSession;
import de.materna.jdec.model.ModelImportException;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Workspace {
	private static final Logger log = Logger.getLogger(Workspace.class);

	private PersistenceFileManager modelManager;
	private Configuration config;
	private PersistenceFileManager configurationManager;
	private AccessLog accessLog;
	private PersistenceFileManager logManager;
	private PersistenceDirectoryManager<PersistedInput> inputManager;
	private PersistenceDirectoryManager<PersistedOutput> outputManager;
	private PersistenceDirectoryManager<PersistedTest> testManager;
	private DecisionSession decisionSession;

	public Workspace(String name) throws IOException {
		modelManager = new PersistenceFileManager(name, "model.dmn");
		configurationManager = new PersistenceFileManager(name, "configuration.json");
		config = new Configuration(name, configurationManager);
		logManager = new PersistenceFileManager(name, "access.log");
		accessLog = new AccessLog(name, logManager);
		inputManager = new PersistenceDirectoryManager<>(name, "inputs", PersistedInput.class);
		outputManager = new PersistenceDirectoryManager<>(name, "outputs", PersistedOutput.class);
		testManager = new PersistenceDirectoryManager<>(name, "tests", PersistedTest.class);
		decisionSession = new DecisionSession();

		if(!configurationManager.fileExists()) {
			//Write default configuration
			config.setCreatedDate(System.currentTimeMillis());
			config.setModifiedDate(config.getCreatedDate());
			configurationManager.persistFile(config.printAsJson());
		} else {
			//Import configuration
			if(!config.deserializeFromJson(configurationManager.getFile())) {
				System.out.println("Failed to load configuration.json");
				//TODO Shut down?
			}
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
		return config;
	}
	
	public AccessLog getAccessLog() {
		return accessLog;
	}
}
