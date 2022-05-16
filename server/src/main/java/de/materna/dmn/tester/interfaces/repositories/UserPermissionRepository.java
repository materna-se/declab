package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;

import de.materna.dmn.tester.beans.userpermission.UserPermission;
import de.materna.dmn.tester.enums.UserPermissionType;

public interface UserPermissionRepository {

	List<UserPermission> findAll();

	UserPermission findByUuid(Long id);

	List<UserPermission> findByUser(String ownerUuid);

	List<UserPermission> findByLaboratory(String laboratoryUuid);

	List<UserPermission> findByWorkspace(String workspaceUuid);

	List<UserPermission> findByType(UserPermissionType type);

	UserPermission findByUserAndLaboratory(String userUuid, String laboratoryUuid);

	UserPermission findByUserAndWorkspace(String userUuid, String workspaceUuid);

	UserPermission findByLaboratoryAndWorkspace(String laboratoryUuid, String workspaceUuid);

	void put(UserPermission relationship);

	void delete(UserPermission relationship);
}