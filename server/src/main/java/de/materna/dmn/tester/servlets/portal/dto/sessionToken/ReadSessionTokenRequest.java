package de.materna.dmn.tester.servlets.portal.dto.sessionToken;

public class ReadSessionTokenRequest {
	private String jwt;

	public ReadSessionTokenRequest() {
	}

	public ReadSessionTokenRequest(String jwt) {
		this.setJwt(jwt);
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
}