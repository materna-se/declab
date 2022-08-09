package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;

import de.materna.dmn.tester.beans.permission.Permission;
import de.materna.dmn.tester.enums.PermissionType;

public interface PermissionRepository {

	List<Permission> findAll();

	Permission findById(Long id);

	List<Permission> findByUserUuid(String userUuid);

	List<Permission> findByLaboratoryUuid(String laboratoryUuid);

	List<Permission> findByWorkspaceUuid(String workspaceUuid);

	List<Permission> findByType(PermissionType type);

	Permission findByUserAndLaboratoryUuids(String userUuid, String laboratoryUuid);

	Permission findByUserAndWorkspaceUuids(String userUuid, String workspaceUuid);

	Permission findByLaboratoryAndWorkspaceUuids(String laboratoryUuid, String workspaceUuid);

	Permission put(Permission relationship);

	Permission create(String userUuid, String laboratoryUuid, String workspaceUuid, PermissionType type);

	boolean delete(Permission relationship);
}