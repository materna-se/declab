package de.materna.dmn.tester.servlets.portal.dto.user;

public class ReadUserRequest {
	private String userUuid;

	private String sessionTokenUuid;

	public ReadUserRequest() {
	}

	public ReadUserRequest(String userUuid, String sessionTokenUuid) {
		this.setUserUuid(userUuid);
		this.setSessionTokenUuid(sessionTokenUuid);
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(String sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}