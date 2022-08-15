package de.materna.dmn.tester.servlets.portal.dto.user;

public class ChangeSystemAdminStateRequest {

	private String userUuid;
	private boolean systemAdmin;

	private String jwt;

	public ChangeSystemAdminStateRequest() {
	}

	public ChangeSystemAdminStateRequest(String userUuid, boolean systemAdmin, String jwt) {
		setUserUuid(userUuid);
		setSystemAdmin(systemAdmin);
		setJwt(jwt);
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

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
}
