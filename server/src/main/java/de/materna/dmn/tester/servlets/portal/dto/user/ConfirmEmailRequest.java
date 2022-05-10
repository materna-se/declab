package de.materna.dmn.tester.servlets.portal.dto.user;

import java.util.UUID;

public class ConfirmEmailRequest {

	private UUID userUuid;
	private String email;
	private boolean confirmed;

	private UUID sessionTokenUuid;

	public ConfirmEmailRequest() {
	}

	public ConfirmEmailRequest(UUID userUuid, String email, UUID sessionTokenUuid) {
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

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public UUID getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(UUID sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
