package de.materna.dmn.tester.servlets.portal.dto.user;

public class ChangePasswordRequest {

	private String userUuid;
	private String passwordNew;
	private String passwordOld;

	private String jwt;

	public ChangePasswordRequest() {
	}

	public ChangePasswordRequest(String userUuid, String passwordNew, String passwordOld, String jwt) {
		setUserUuid(userUuid);
		setPasswordNew(passwordNew);
		setPasswordOld(passwordOld);
		setJwt(jwt);
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

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
}
