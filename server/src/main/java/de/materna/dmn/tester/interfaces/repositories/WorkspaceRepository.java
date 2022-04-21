package de.materna.dmn.tester.interfaces.repositories;

import javax.enterprise.context.ApplicationScoped;

import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.enums.VisabilityType;

@ApplicationScoped
public interface WorkspaceRepository {

	public Workspace findByUuid(String uuid);

	public Workspace put(Workspace workspace);

	Workspace create(String name, String description, VisabilityType visability);

	public void delete(Workspace workspace);

}
