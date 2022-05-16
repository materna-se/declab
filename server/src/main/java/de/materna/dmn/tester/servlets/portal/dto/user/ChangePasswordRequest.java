package de.materna.dmn.tester.servlets.portal.dto.user;

public class ChangePasswordRequest {

	private String userUuid;
	private String passwordNew;
	private String passwordOld;

	private String sessionTokenUuid;

	public ChangePasswordRequest() {
	}

	public ChangePasswordRequest(String userUuid, String passwordNew, String passwordOld, String sessionTokenUuid) {
		setUserUuid(userUuid);
		setPasswordNew(passwordNew);
		setPasswordOld(passwordOld);
		setSessionTokenUuid(sessionTokenUuid);
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
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

	public String getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(String sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
