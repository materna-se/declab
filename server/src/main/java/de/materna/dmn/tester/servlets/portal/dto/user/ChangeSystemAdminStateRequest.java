package de.materna.dmn.tester.servlets.portal.dto.user;

public class ChangeSystemAdminStateRequest {

	private String userUuid;
	private boolean systemAdmin;

	private String sessionTokenUuid;

	public ChangeSystemAdminStateRequest() {
	}

	public ChangeSystemAdminStateRequest(String userUuid, boolean systemAdmin, String sessionTokenUuid) {
		setUserUuid(userUuid);
		setSystemAdmin(systemAdmin);
		setSessionTokenUuid(sessionTokenUuid);
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public boolean isSystemAdmin() {
		return systemAdmin;
	}

	public void setSystemAdmin(boolean systemAdmin) {
		this.systemAdmin = systemAdmin;
	}

	public String getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(String sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
