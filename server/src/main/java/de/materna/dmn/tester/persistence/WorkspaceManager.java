package de.materna.dmn.tester.persistence;

import de.materna.dmn.tester.beans.Workspace;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class WorkspaceManager {
	private static WorkspaceManager instance;

	private Map<String, Workspace> workspaces = new HashMap<>();

	private WorkspaceManager() {
	}

	public Workspace get(String name) throws IOException {
		Workspace workspace = workspaces.get(name);
		if (workspace == null) {
			workspace = new Workspace(name);
			workspaces.put(name, workspace);
		}
		return workspace;
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
