package de.materna.dmn.tester.servlets.portal.dto.user;

import java.util.UUID;

public class ChangePasswordRequest {

	private UUID userUuid;
	private String passwordNew;
	private String passwordOld;

	private UUID sessionTokenUuid;

	public ChangePasswordRequest() {
	}

	public ChangePasswordRequest(UUID userUuid, String passwordNew, String passwordOld, UUID sessionTokenUuid) {
		setUserUuid(userUuid);
		setPasswordNew(passwordNew);
		setPasswordOld(passwordOld);
		setSessionTokenUuid(sessionTokenUuid);
	}

	public UUID getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(UUID userUuid) {
		this.userUuid = userUuid;
	}

	public String getPasswordNew() {
		return passwordNew;
	}

	public void setPasswordNew(String passwordNew) {
		this.passwordNew = passwordNew;
	}

	public String getPasswordOld() {
		return passwordOld;
	}

	public void setPasswordOld(String passwordOld) {
		this.passwordOld = passwordOld;
	}

	public UUID getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(UUID sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
