package de.materna.dmn.tester.servlets.portal.dto.user;

import java.util.UUID;

public class ChangePasswordRequest {

	private UUID userUuid;
	private String password;

	private UUID sessionTokenUuid;

	public ChangePasswordRequest() {
	}

	public ChangePasswordRequest(UUID userUuid, String password, UUID sessionTokenUuid) {
		setUserUuid(userUuid);
		setPassword(password);
		setSessionTokenUuid(sessionTokenUuid);
	}

	public UUID getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(UUID userUuid) {
		this.userUuid = userUuid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UUID getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(UUID sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
