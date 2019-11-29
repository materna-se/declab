package de.materna.dmn.tester.servlets.workspace.beans;

import de.materna.dmn.tester.persistence.PersistenceDirectoryManager;
import de.materna.dmn.tester.persistence.PersistenceFileManager;
import de.materna.dmn.tester.servlets.input.beans.PersistedInput;
import de.materna.dmn.tester.servlets.output.beans.PersistedOutput;
import de.materna.dmn.tester.servlets.test.beans.PersistedTest;
import de.materna.jdec.DecisionSession;
import de.materna.jdec.model.ImportException;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Workspace {
	private static final Logger log = Logger.getLogger(Workspace.class);

	private PersistenceFileManager modelManager;
	private PersistenceDirectoryManager<PersistedInput> inputManager;
	private PersistenceDirectoryManager<PersistedOutput> outputManager;
	private PersistenceDirectoryManager<PersistedTest> testManager;
	private DecisionSession decisionSession;

	public Workspace(String name) throws IOException {
		modelManager = new PersistenceFileManager(name, "model.dmn");
		inputManager = new PersistenceDirectoryManager<>(name, "inputs", PersistedInput.class);
		outputManager = new PersistenceDirectoryManager<>(name, "outputs", PersistedOutput.class);
		testManager = new PersistenceDirectoryManager<>(name, "tests", PersistedTest.class);
		decisionSession = new DecisionSession();

		try {
			decisionSession.importModel("main", "main", modelManager.getFile());
		}
		catch (IOException | ImportException e) {
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
}
