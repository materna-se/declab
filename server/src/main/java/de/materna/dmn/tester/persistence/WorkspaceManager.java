package de.materna.dmn.tester.persistence;

import de.materna.dmn.tester.helpers.HashingHelper;
import de.materna.dmn.tester.servlets.workspace.beans.Configuration;
import de.materna.dmn.tester.servlets.workspace.beans.PublicConfiguration.Access;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.kie.dmn.api.core.DMNModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class WorkspaceManager {
	private static final Logger log = Logger.getLogger(WorkspaceManager.class);
	private static WorkspaceManager instance;

	private Map<String, Workspace> workspaces = new HashMap<>();

	private WorkspaceManager() throws IOException {
		Path path = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces");
		if(!Files.exists(path)) {
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

	public Workspace get(String workspaceUUID) throws IOException {
		Workspace workspace = workspaces.get(workspaceUUID);
		if (workspace == null) {
			throw new IOException("Workspace " + workspaceUUID + " can't be found.");
		}

		return workspace;
	}

	public void indexAll() throws IOException {
		File dir = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces").toFile();
		for (File subdir : dir.listFiles()) {
			try {
				index(subdir.getName());
			} catch(IOException e) {
				log.error("Could not index workspace " + subdir.getName());
				e.printStackTrace();
			}
		}
	}

	public void index(String workspaceUUID) throws IOException {
		//Upgrade path from version 0 to 1
		File dir = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces", workspaceUUID).toFile();
		File config = new File(dir + File.separator + "configuration.json");
		if (!config.exists()) {
			log.info("Workspace " + workspaceUUID + " needs to be upgraded from version 0 to 1.");

			String generatedUUID = UUID.randomUUID().toString();
			Files.move(Paths.get(dir.getAbsolutePath()), Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces", generatedUUID));
			dir = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces", generatedUUID).toFile();
			
			// Create configuration.json
			PersistenceFileManager configManager = new PersistenceFileManager(generatedUUID, "configuration.json");
			Configuration configuration = new Configuration(configManager);
			configuration.setVersion(1);
			configuration.setName(workspaceUUID);
			configuration.setAccess(Access.PUBLIC);
			try {
				configuration.setSalt(HashingHelper.getInstance().generateSalt());
			}
			catch (NoSuchAlgorithmException exception) {
				throw new IOException("Could not instantiate SecureRandom, salt is null");
			}
			configuration.setCreatedDate(System.currentTimeMillis());
			configuration.setModifiedDate(configuration.getCreatedDate());
			configuration.serialize();
			
			workspaceUUID = generatedUUID;
		}

		Workspace workspace = new Workspace(workspaceUUID);
		
		//Upgrade path from version 1 to 2
		if(workspace.getConfig().getVersion() == 1) {
			log.info("Workspace " + workspaceUUID + " needs to be upgraded from version 1 to 2.");
			
			//Check if workspace has any models
			File file = new File(dir.getAbsolutePath() + File.separator + "model.dmn");
			if(file.exists()) {
				//Generate model UUID, move model file into models folder, rename model
				String modelUUID = UUID.randomUUID().toString();
				File modelDir = Paths.get(dir.getAbsolutePath(), "models").toFile();
				if(!modelDir.exists()) {
					modelDir.mkdir();
				}
				Path check = Files.move(Paths.get(file.getAbsolutePath()), Paths.get(modelDir.getAbsolutePath() + File.separator + modelUUID + ".dmn"));
				if(check == null) {
					throw new RuntimeException();
				}
				
				//Update reference to model file
				file = check.toFile();
				
				//Import model in order to obtain namespace and name
				workspace.getDecisionSession().importModel("main", "main", new String(Files.readAllBytes(file.toPath())));
				DMNModel importedModel = workspace.getDecisionSession().getRuntime().getModels().get(0);
				
				//Update the configuration with obtained information
				LinkedList<HashMap<String, String>> models = new LinkedList<HashMap<String, String>>();
				HashMap<String, String> modelMap = new HashMap<String, String>();
				modelMap.put("namespace", importedModel.getNamespace());
				modelMap.put("name", importedModel.getName());
				modelMap.put("uuid", modelUUID);
				models.add(modelMap);
				workspace.getConfig().setModels(models);
				
				//Recreate decision session with correct information
				workspace.clearDecisionSession();
				workspace.getDecisionSession().importModel(importedModel.getNamespace(), importedModel.getName(), new String(Files.readAllBytes(file.toPath())));
				
				//GC
				importedModel = null;
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
