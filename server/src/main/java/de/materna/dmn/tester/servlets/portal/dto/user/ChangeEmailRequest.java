package de.materna.dmn.tester.servlets.portal.dto.user;

public class ChangeEmailRequest {

	private String userUuid;
	private String email;

	private String sessionTokenUuid;

	public ChangeEmailRequest() {
	}

	public ChangeEmailRequest(String userUuid, String email, String sessionTokenUuid) {
		setUserUuid(userUuid);
		setEmail(email);
		setSessionTokenUuid(sessionTokenUuid);
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(String sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
