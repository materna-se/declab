package de.materna.dmn.tester.persistence;

import de.materna.dmn.tester.helpers.HashingHelper;
import de.materna.dmn.tester.servlets.workspace.beans.AccessLog;
import de.materna.dmn.tester.servlets.workspace.beans.Configuration;
import de.materna.dmn.tester.servlets.workspace.beans.PublicConfiguration.Access;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.ws.rs.NotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class WorkspaceManager {
	private static final Logger log = Logger.getLogger(WorkspaceManager.class);
	private static WorkspaceManager instance;

	private Map<String, Workspace> workspaces = new HashMap<>();

	private WorkspaceManager() {
	}

	public static synchronized WorkspaceManager getInstance() {
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

	public void index() throws IOException {
		File dir = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces").toFile();
		for (File subdir : dir.listFiles()) {
			String workspaceName = subdir.getName();

			// Check for version 0 workspaces and upgrade them.
			PersistenceFileManager configurationManager = new PersistenceFileManager(workspaceName, "configuration.json");
			if (!configurationManager.fileExists()) {
				log.info("Workspace " + workspaceName + " needs to be upgraded from version 0 to 1.");

				// Create configuration.json
				Configuration configuration = new Configuration();
				configuration.setVersion(1);
				configuration.setName(workspaceName);
				configuration.setAccess(Access.PUBLIC);
				try {
					configuration.setSalt(HashingHelper.getInstance().generateSalt());
				}
				catch (NoSuchAlgorithmException exception) {
					throw new IOException("Could not instantiate SecureRandom, salt is null");
				}
				configuration.setCreatedDate(System.currentTimeMillis());
				configuration.setModifiedDate(configuration.getCreatedDate());
				configurationManager.persistFile(configuration.toJson());

				// Move the workspace to a new directory because version 1 directories should only be named after a uuid.
				String workspaceUUID = UUID.randomUUID().toString();
				Files.move(subdir.toPath(), new File(dir, workspaceUUID).toPath());

				Workspace workspace = new Workspace(workspaceUUID);
				workspaces.put(workspaceUUID, workspace);
				continue;
			}

			Workspace workspace = new Workspace(workspaceName);
			workspaces.put(workspaceName, workspace);
		}
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

	public boolean exists(String workspaceUUID) {
		File dir = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces").toFile();
		for (File subdir : dir.listFiles()) {
			if (subdir.getName().equals(workspaceUUID)) {
				return true;
			}
		}
		return false;
	}

	public void add(String uuid, Workspace workspace) {
		workspaces.put(uuid, workspace);
	}

	public void remove(String uuid) throws IOException {
		FileUtils.deleteDirectory(Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces", uuid).toFile());
		invalidate(uuid);
	}

	public void invalidate(String uuid) {
		workspaces.remove(uuid);
	}
}
