package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;
import java.util.UUID;

import de.materna.dmn.tester.beans.userpermission.UserPermission;
import de.materna.dmn.tester.enums.UserPermissionType;

public interface UserPermissionRepository {

	List<UserPermission> findAll();

	UserPermission findByUuid(Long id);

	List<UserPermission> findByUser(UUID userUuid);

	List<UserPermission> findByLaboratory(UUID laboratoryUuid);

	List<UserPermission> findByWorkspace(UUID workspaceUuid);

	List<UserPermission> findByType(UserPermissionType type);

	UserPermission findByUserAndLaboratory(UUID userUuid, UUID laboratoryUuid);

	UserPermission findByUserAndWorkspace(UUID userUuid, UUID workspaceUuid);

	UserPermission findByLaboratoryAndWorkspace(UUID laboratoryUuid, UUID workspaceUuid);

	void put(UserPermission relationship);

	void delete(UserPermission relationship);
}