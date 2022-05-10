package de.materna.dmn.tester.enums;

public class UserPermissionGroup {
	public UserPermissionGroup() {
	}

	public static final UserPermissionType[] OWNER = { UserPermissionType.OWNER };
	public static final UserPermissionType[] ADMINISTRATOR = { UserPermissionType.OWNER,
			UserPermissionType.ADMINISTRATOR };
	public static final UserPermissionType[] CONTRIBUTOR = { UserPermissionType.OWNER, UserPermissionType.ADMINISTRATOR,
			UserPermissionType.CONTRIBUTOR };
	public static final UserPermissionType[] GUEST = { UserPermissionType.OWNER, UserPermissionType.ADMINISTRATOR,
			UserPermissionType.CONTRIBUTOR, UserPermissionType.GUEST };
}
