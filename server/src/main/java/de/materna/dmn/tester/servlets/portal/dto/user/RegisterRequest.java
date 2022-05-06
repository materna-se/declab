package de.materna.dmn.tester.servlets.portal.dto.user;

public class RegisterRequest {
	private String email;
	private String username;
	private String password;

	public RegisterRequest() {
	}

	public RegisterRequest(String email, String username, String password) {
		setEmail(email);
		setUsername(username);
		setPassword(password);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
