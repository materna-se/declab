package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;

import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.enums.VisabilityType;
import de.materna.dmn.tester.servlets.exceptions.database.WorkspaceNotFoundException;

public interface WorkspaceRepository {

	List<Workspace> getAll();

	Workspace getByUuid(String workspaceUuid) throws WorkspaceNotFoundException;

	List<Workspace> getByName(String name);

	List<Workspace> getByVisability(VisabilityType visability);

	List<Workspace> getByUser(String ownerUuid);

	List<Workspace> getByLaboratory(String laboratoryUuid);

	Workspace put(Workspace workspace) throws WorkspaceNotFoundException;

	Workspace create(String name, String description, VisabilityType visability, String laboratoryUuid)
			throws WorkspaceNotFoundException;

	boolean delete(Workspace workspace);

}