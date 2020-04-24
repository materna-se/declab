package de.materna.dmn.tester.persistence;

import de.materna.dmn.tester.helpers.HashingHelper;
import de.materna.dmn.tester.servlets.workspace.beans.Configuration;
import de.materna.dmn.tester.servlets.workspace.beans.PublicConfiguration.Access;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.kie.dmn.api.core.DMNModel;

import javax.ws.rs.NotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

public class WorkspaceManager {
	private static final Logger log = Logger.getLogger(WorkspaceManager.class);
	private static WorkspaceManager instance;

	private Map<String, Workspace> workspaces = new HashMap<>();

	private WorkspaceManager() throws IOException {
		Path path = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces");
		if (!Files.exists(path)) {
			Files.createDirectories(path);
		}
	}

	public static synchronized WorkspaceManager getInstance() throws IOException {
		if (instance == null) {
			instance = new WorkspaceManager();
		}
		return instance;
	}

	public Map<String, Workspace> getAll() {
		return workspaces;
	}

	public Workspace get(String workspaceUUID) {
		Workspace workspace = workspaces.get(workspaceUUID);
		if (workspace == null) {
			throw new NotFoundException("Workspace " + workspaceUUID + " can't be found.");
		}

		return workspace;
	}

	public void indexAll() {
		File dir = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces").toFile();
		for (File subdir : dir.listFiles()) {
			try {
				index(subdir.getName());
			}
			catch (IOException e) {
				log.error("Could not index workspace " + subdir.getName(), e);
			}
		}
	}

	public void index(String workspaceUUID) throws IOException {
		//Upgrade path from version 0 to 1
		File workspaceDirectory = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces", workspaceUUID).toFile();
		File configurationFile = new File(workspaceDirectory + File.separator + "configuration.json");
		if (!configurationFile.exists()) {
			log.info("Workspace " + workspaceUUID + " needs to be upgraded from version 0 to 1.");

			String generatedUUID = UUID.randomUUID().toString();
			workspaceDirectory = Files.move(Paths.get(workspaceDirectory.getAbsolutePath()), Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces", generatedUUID)).toFile();

			// Create configuration.json
			PersistenceFileManager configManager = new PersistenceFileManager(generatedUUID, "configuration.json");
			Configuration configuration = new Configuration(configManager);
			configuration.setVersion(1);
			configuration.setName(workspaceUUID);
			configuration.setAccess(Access.PUBLIC);
			configuration.setSalt(HashingHelper.getInstance().generateSalt());
			configuration.setCreatedDate(System.currentTimeMillis());
			configuration.setModifiedDate(configuration.getCreatedDate());
			configuration.serialize();

			workspaceUUID = generatedUUID;
		}

		Workspace workspace = new Workspace(workspaceUUID);

		//Upgrade path from version 1 to 2
		if (workspace.getConfig().getVersion() == 1) {
			log.info("Workspace " + workspaceUUID + " needs to be upgraded from version 1 to 2.");

			// Create models directory.
			File modelDirectory = Paths.get(workspaceDirectory.getAbsolutePath(), "models").toFile();
			if (!modelDirectory.exists()) {
				modelDirectory.mkdir();
			}

			// Check if the workspace has any models. If it does, we will move it to the new models directory.
			File modelFile = new File(workspaceDirectory.getAbsolutePath() + File.separator + "model.dmn");
			if (modelFile.exists()) {
				// Models are no longer called model.dmn, they are identified by an UUID.
				String modelUUID = UUID.randomUUID().toString();

				// Update the reference to the moved model file.
				Path check = Files.move(Paths.get(modelFile.getAbsolutePath()), Paths.get(modelDirectory.getAbsolutePath() + File.separator + modelUUID + ".dmn"));
				modelFile = check.toFile();

				// Import model temporarily in order to obtain namespace and name.
				workspace.clearDecisionSession();
				workspace.getDecisionSession().importModel("main", "main", new String(Files.readAllBytes(modelFile.toPath()), StandardCharsets.UTF_8));
				DMNModel importedModel = workspace.getDecisionSession().getRuntime().getModels().get(0);

				// The configuration needs to be updated with obtained information.
				List<Map<String, String>> models = new LinkedList<>();
				HashMap<String, String> model = new HashMap<>();
				model.put("namespace", importedModel.getNamespace());
				model.put("name", importedModel.getName());
				model.put("uuid", modelUUID);
				models.add(model);
				workspace.getConfig().setModels(models);

				// Recreate decision session with correct information
				workspace.clearDecisionSession();
				workspace.getDecisionSession().importModel(importedModel.getNamespace(), importedModel.getName(), new String(Files.readAllBytes(modelFile.toPath()), StandardCharsets.UTF_8));
			}

			workspace.getConfig().setVersion(2);
			workspace.getConfig().serialize();
		}

		workspaces.put(workspaceUUID, workspace);
	}

	public Map<String, Workspace> search(String workspaceName) {
		Map<String, Workspace> matches = new LinkedHashMap<>();
		for (Entry<String, Workspace> entry : workspaces.entrySet()) {
			if (entry.getValue().getConfig().getName().toLowerCase().contains(workspaceName.toLowerCase())) {
				matches.put(entry.getKey(), entry.getValue());
			}
		}
		return matches;
	}

	public boolean has(String workspaceUUID) {
		return workspaces.containsKey(workspaceUUID);
	}

	public void add(String uuid, Workspace workspace) {
		workspaces.put(uuid, workspace);
	}

	public void remove(String uuid) throws IOException {
		FileUtils.deleteDirectory(Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces", uuid).toFile());
		workspaces.remove(uuid);
	}
}
