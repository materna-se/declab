package de.materna.dmn.tester.servlets.portal.dto.user;

public class UpdateUserRequest {

	private String userUuid;
	private String email;
	private String username;
	private String firstname;
	private String lastname;

	private String sessionTokenUuid;

	public UpdateUserRequest() {
	}

	public UpdateUserRequest(String userUuid, String email, String username, String firstname, String lastname,
			String sessionTokenUuid) {
		setUserUuid(userUuid);
		setEmail(email);
		setUsername(username);
		setFirstname(firstname);
		setLastname(lastname);
		setSessionTokenUuid(sessionTokenUuid);
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(String sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}