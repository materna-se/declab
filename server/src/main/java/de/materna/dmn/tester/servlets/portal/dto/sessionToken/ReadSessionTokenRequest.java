package de.materna.dmn.tester.servlets.portal.dto.sessionToken;

public class ReadSessionTokenRequest {
	private String sessionTokenUuid;

	public ReadSessionTokenRequest() {
	}

	public ReadSessionTokenRequest(String userUuid, String sessionTokenUuid) {
		this.setSessionTokenUuid(sessionTokenUuid);
	}

	public String getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(String sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
