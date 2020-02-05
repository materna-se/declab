package de.materna.dmn.tester.persistence;

import de.materna.dmn.tester.servlets.workspace.beans.Configuration;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.dmn.tester.servlets.workspace.beans.PublicConfiguration.Access;
import de.materna.jdec.serialization.SerializationHelper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

public class WorkspaceManager {
	private static WorkspaceManager instance;

	private Map<String, Workspace> workspaces = new HashMap<>();

	private WorkspaceManager() {
	}
	
	public Workspace getByUUID(String workspaceUUID) throws IOException {
		Workspace workspace = workspaces.get(workspaceUUID);
		
		if (workspace == null && exists(workspaceUUID)) {
			workspace = new Workspace(workspaceUUID);
		}
		return workspace;
	}
	
	public Map<String, Workspace> getByName(String workspaceName) {
		Map<String, Workspace> matches = new HashMap<>();
		for (Entry<String, Workspace> entry : workspaces.entrySet()) {
			if(entry.getValue().getConfig().getName().equalsIgnoreCase(workspaceName)) {
				matches.put(entry.getKey(), entry.getValue());
			}
		}
		return matches;
	}

	public void index() throws IOException {
		File dir = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces").toFile();
		for(File subdir : dir.listFiles()) {
			//Check for V0 workspaces and upgrade them if any are found
			File subdirConfig = new File(subdir.getAbsolutePath() + File.separator + "configuration.json");
			if(!subdirConfig.exists() && subdir.listFiles().length > 0) {
				String workspaceName = subdir.getName();
				String uuid = UUID.randomUUID().toString();
				Files.move(subdir.toPath(), new File(dir.getAbsolutePath() + File.separator + uuid).toPath());
				
				Workspace workspace = new Workspace(uuid);
				workspaces.put(subdir.getName(), workspace);

				workspace.getConfig().setName(workspaceName);
				workspace.getConfig().serialize();
			} else {
				Workspace workspace = new Workspace(subdir.getName());
				workspaces.put(subdir.getName(), workspace);
			}
		}
	}
	
	public boolean exists(String workspaceUUID) {
		File dir = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces").toFile();
		for(File subdir : dir.listFiles()) {
			if(subdir.getName().equals(workspaceUUID)) {
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

	public static synchronized WorkspaceManager getInstance() {
		if (instance == null) {
			instance = new WorkspaceManager();
		}
		return instance;
	}

	public Map<String, Workspace> getWorkspaces() {
		return workspaces;
	}
}
