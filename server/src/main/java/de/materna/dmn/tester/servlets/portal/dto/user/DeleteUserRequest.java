package de.materna.dmn.tester.servlets.portal.dto.user;

import java.util.UUID;

public class DeleteUserRequest {
	private UUID userUuid;

	private UUID sessionTokenUuid;

	public DeleteUserRequest() {
	}

	public DeleteUserRequest(UUID userUuid, UUID sessionTokenUuid) {
		this.setUserUuid(userUuid);
		this.setSessionTokenUuid(sessionTokenUuid);
	}

	public UUID getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(UUID userUuid) {
		this.userUuid = userUuid;
	}

	public UUID getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(UUID sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}

}
