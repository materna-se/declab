package de.materna.dmn.tester.servlets.portal.dto.sessionToken;

public class UpdateSessionTokenRequest {
	private String jwt;

	public UpdateSessionTokenRequest() {
	}

	public UpdateSessionTokenRequest(String jwt) {
		this.setJwt(jwt);
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
}