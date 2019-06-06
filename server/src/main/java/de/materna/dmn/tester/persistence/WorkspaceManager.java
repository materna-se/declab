package de.materna.dmn.tester.persistence;

import de.materna.dmn.tester.servlets.model.beans.Workspace;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WorkspaceManager {
	private static WorkspaceManager instance;

	private Map<String, Workspace> workspaces = new HashMap<>();

	private WorkspaceManager() {
	}

	public Workspace getWorkspace(String name) throws IOException {
		Workspace workspace = workspaces.get(name);
		if (workspace == null) {
			workspace = new Workspace(name);
			workspaces.put(name, workspace);
		}
		return workspace;
	}

	public static synchronized WorkspaceManager getInstance() {
		if (instance == null) {
			instance = new WorkspaceManager();
		}
		return instance;
	}
}
