package de.materna.dmn.tester.servlets.portal.dto.user;

import java.util.UUID;

public class UpdateUserRequest {

	private UUID userUuid;
	private String email;
	private String username;
	private String firstname;
	private String lastname;

	private UUID sessionTokenUuid;

	public UpdateUserRequest() {
	}

	public UpdateUserRequest(UUID userUuid, String email, String username, String firstname, String lastname,
			UUID sessionTokenUuid) {
		setUserUuid(userUuid);
		setEmail(email);
		setUsername(username);
		setFirstname(firstname);
		setLastname(lastname);
		setSessionTokenUuid(sessionTokenUuid);
	}

	public UUID getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(UUID userUuid) {
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

	public UUID getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(UUID sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
