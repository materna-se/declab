package de.materna.dmn.tester.interfaces.repositories;

import javax.enterprise.context.ApplicationScoped;

import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.enums.VisabilityType;

@ApplicationScoped
public interface WorkspaceRepository {

	Workspace findByUuid(String uuid);

	Workspace put(Workspace workspace);

	Workspace create(String name, String description, VisabilityType visability);

	void delete(Workspace workspace);

}