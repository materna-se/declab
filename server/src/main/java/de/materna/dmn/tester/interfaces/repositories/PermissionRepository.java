package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;

import de.materna.dmn.tester.beans.permission.Permission;
import de.materna.dmn.tester.enums.PermissionType;

public interface PermissionRepository {

	List<Permission> getAll();

	Permission getById(Long id);

	List<Permission> getByUserUuid(String userUuid);

	List<Permission> getByLaboratoryUuid(String laboratoryUuid);

	List<Permission> getByWorkspaceUuid(String workspaceUuid);

	List<Permission> getByType(PermissionType type);

	Permission getByUserAndLaboratoryUuids(String userUuid, String laboratoryUuid);

	Permission getByUserAndWorkspaceUuids(String userUuid, String workspaceUuid);

	Permission getByLaboratoryAndWorkspaceUuids(String laboratoryUuid, String workspaceUuid);

	Permission put(Permission relationship);

	Permission create(String userUuid, String laboratoryUuid, String workspaceUuid, PermissionType type);

	boolean delete(Permission relationship);
}