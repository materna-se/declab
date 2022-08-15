package de.materna.dmn.tester.servlets.portal.dto.user;

public class ChangeEmailRequest {

	private String userUuid;
	private String email;

	private String jwt;

	public ChangeEmailRequest() {
	}

	public ChangeEmailRequest(String userUuid, String email, String jwt) {
		setUserUuid(userUuid);
		setEmail(email);
		setJwt(jwt);
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

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
}
