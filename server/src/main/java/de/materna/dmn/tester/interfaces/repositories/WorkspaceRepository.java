package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;

import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.enums.VisabilityType;

public interface WorkspaceRepository {

	List<Workspace> getAll();

	Workspace getByUuid(String workspaceUuid);

	List<Workspace> getByName(String name);

	List<Workspace> getByVisability(VisabilityType visability);

	List<Workspace> getByUser(String ownerUuid);

	List<Workspace> getByLaboratory(String laboratoryUuid);

	Workspace put(Workspace workspace);

	Workspace create(String name, String description, VisabilityType visability, String laboratoryUuid);

	boolean delete(Workspace workspace);

}