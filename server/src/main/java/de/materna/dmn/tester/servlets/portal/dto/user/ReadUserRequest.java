package de.materna.dmn.tester.servlets.portal.dto.user;

public class ReadUserRequest {
	private String userUuid;

	private String jwt;

	public ReadUserRequest() {
	}

	public ReadUserRequest(String userUuid, String jwt) {
		this.setUserUuid(userUuid);
		this.setJwt(jwt);
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
}
