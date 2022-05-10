package de.materna.dmn.tester.servlets.portal.dto.user;

import java.util.UUID;

public class ChangeSystemAdminStateRequest {

	private UUID userUuid;
	private boolean systemAdmin;

	private UUID sessionTokenUuid;

	public ChangeSystemAdminStateRequest() {
	}

	public ChangeSystemAdminStateRequest(UUID userUuid, boolean systemAdmin, UUID sessionTokenUuid) {
		setUserUuid(userUuid);
		setSystemAdmin(systemAdmin);
		setSessionTokenUuid(sessionTokenUuid);
	}

	public UUID getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(UUID userUuid) {
		this.userUuid = userUuid;
	}

	public boolean isSystemAdmin() {
		return systemAdmin;
	}

	public void setSystemAdmin(boolean systemAdmin) {
		this.systemAdmin = systemAdmin;
	}

	public UUID getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(UUID sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
