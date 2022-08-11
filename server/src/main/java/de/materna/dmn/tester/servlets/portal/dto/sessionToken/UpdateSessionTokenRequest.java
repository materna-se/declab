package de.materna.dmn.tester.servlets.portal.dto.sessionToken;

public class UpdateSessionTokenRequest {
	private String sessionTokenUuid;

	public UpdateSessionTokenRequest() {
	}

	public UpdateSessionTokenRequest(String userUuid, String sessionTokenUuid) {
		this.setSessionTokenUuid(sessionTokenUuid);
	}

	public String getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(String sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
