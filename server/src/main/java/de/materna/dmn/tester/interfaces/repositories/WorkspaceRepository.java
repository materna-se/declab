package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;

import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.enums.VisabilityType;

public interface WorkspaceRepository {

	List<Workspace> findAll();

	Workspace findByUuid(String workspaceUuid);

	List<Workspace> findByName(String name);

	List<Workspace> findByVisability(VisabilityType visability);

	List<Workspace> findByUser(String ownerUuid);

	List<Workspace> findByLaboratory(String laboratoryUuid);

	Workspace put(Workspace workspace);

	Workspace create(String name, String description, VisabilityType visability, String laboratoryUuid);

	boolean delete(Workspace workspace);

}