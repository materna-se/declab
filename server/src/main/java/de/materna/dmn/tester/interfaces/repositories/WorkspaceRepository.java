package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.enums.VisabilityType;

@ApplicationScoped
public interface WorkspaceRepository {

	List<Workspace> findAll();

	Workspace findByUuid(UUID workspaceUuid);

	List<Workspace> findByName(String name);

	List<Workspace> findByVisability(VisabilityType visability);

	List<Workspace> findByUser(UUID ownerUuid);

	List<Workspace> findByLaboratory(UUID laboratoryUuid);

	Workspace put(Workspace workspace);

	Workspace create(String name, String description, VisabilityType visability, UUID laboratoryUuid);

	boolean delete(Workspace workspace);

}