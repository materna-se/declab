package de.materna.dmn.tester.beans.permission;

import de.materna.dmn.tester.enums.PermissionType;

public class PermissionGroup {
	public PermissionGroup() {
	}

	public static final PermissionType[] OWNER = { PermissionType.OWNER };
	public static final PermissionType[] ADMINISTRATOR = { PermissionType.OWNER, PermissionType.ADMINISTRATOR };
	public static final PermissionType[] CONTRIBUTOR = { PermissionType.OWNER, PermissionType.ADMINISTRATOR,
			PermissionType.CONTRIBUTOR };
	public static final PermissionType[] GUEST = { PermissionType.OWNER, PermissionType.ADMINISTRATOR,
			PermissionType.CONTRIBUTOR, PermissionType.GUEST };
}
