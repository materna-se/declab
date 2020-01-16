package de.materna.dmn.tester.persistence;

import de.materna.dmn.tester.servlets.workspace.beans.Configuration;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.serialization.SerializationHelper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class WorkspaceManager {
	private static WorkspaceManager instance;

	private Map<String, Workspace> workspaces = new HashMap<>();

	private WorkspaceManager() {
		try {
			indexAllWorkspaces();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Workspace getByUUID(String workspaceUUID) throws IOException {
		Workspace workspace = workspaces.get(workspaceUUID);
		
		if (workspace == null && workspaceExists(workspaceUUID)) {
			workspace = new Workspace(workspaceUUID);
		}
		return workspace;
	}
	
	public Map<String, Workspace> getByName(String workspaceName) throws IOException {
		Map<String, Workspace> matches = new HashMap<String, Workspace>();
		for (Entry<String, Workspace> entry : workspaces.entrySet()) {
			if(entry.getValue().getConfig().getName().equalsIgnoreCase(workspaceName)) {
				matches.put(entry.getKey(), entry.getValue());
			}
		}
		return matches;
	}
	
	public Map<String, Workspace> getWorkspaces() {
		return workspaces;
	}
	
	public void indexAllWorkspaces() throws IOException {
		File dir = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces").toFile();
		for(File subdir : dir.listFiles()) {
			Workspace workspace = new Workspace(subdir.getName());
			workspaces.put(subdir.getName(), workspace);
		}
	}
	
	public boolean workspaceExists(String workspaceUUID) throws IOException {
		File dir = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces").toFile();
		for(File subdir : dir.listFiles()) {
			if(subdir.getName().equals(workspaceUUID)) {
				return true;
			}
		}
		return false;
	}

	public void remove(String name) throws IOException {
		FileUtils.deleteDirectory(Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces", name).toFile());
		invalidate(name);
	}

	public void invalidate(String name) {
		workspaces.remove(name);
	}

	public static synchronized WorkspaceManager getInstance() {
		if (instance == null) {
			instance = new WorkspaceManager();
		}
		return instance;
	}
}
