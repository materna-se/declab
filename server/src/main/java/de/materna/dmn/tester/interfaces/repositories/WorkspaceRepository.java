package de.materna.dmn.tester.interfaces.repositories;

import javax.enterprise.context.ApplicationScoped;

import de.materna.dmn.tester.beans.workspace.Workspace;

@ApplicationScoped
public interface WorkspaceRepository {

	public Workspace findByUuid(String uuid);

	public void put(Workspace workspace);

	public void delete(Workspace workspace);
}
