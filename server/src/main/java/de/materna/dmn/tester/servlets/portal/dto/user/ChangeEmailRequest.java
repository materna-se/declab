package de.materna.dmn.tester.servlets.portal.dto.user;

import java.util.UUID;

public class ChangeEmailRequest {

	private UUID userUuid;
	private String email;

	private UUID sessionTokenUuid;

	public ChangeEmailRequest() {
	}

	public ChangeEmailRequest(UUID userUuid, String email, UUID sessionTokenUuid) {
		setUserUuid(userUuid);
		setEmail(email);
		setSessionTokenUuid(sessionTokenUuid);
	}

	public UUID getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(UUID userUuid) {
		this.userUuid = userUuid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UUID getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(UUID sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
