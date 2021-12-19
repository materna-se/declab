package de.materna.dmn.tester.beans;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
public class User {
	@Id
	@Column(name = "uuid", unique = true, nullable = false)
	private String uuid;
	@Column(name = "email", unique = true, nullable = false)
	@Email(message = "Email address must be valid")
	@NotEmpty(message = "E-Mail cannot be empty")
	private String email;
	@Column(name = "userName", nullable = false)
	@NotEmpty(message = "Username cannot be empty")
	private String userName;
	@Column(name = "firstName")
	private String firstName;
	@Column(name = "lastName")
	private String lastName;
	@Column(name = "password", nullable = false)
	@NotEmpty(message = "Password cannot be empty")
	private String password;
	@Column(name = "imageId")
	private String imageId;
	@Column(name = "registrationDateTime")
	private LocalDateTime registrationDateTime;

	public User(String email, String userName, String firstName, String lastName, String password, String imageId) {
		this.uuid = UUID.randomUUID().toString();
		this.registrationDateTime = LocalDateTime.now();
		this.email = email;
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.imageId = imageId;
	}

	public String getUuid() {
		return uuid;
	}

	public String getEmail() {
		return email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUsername() {
		return userName;
	}

	public void setUsername(String username) {
		this.userName = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public LocalDateTime getRegistrationDateTime() {
		return registrationDateTime;
	}
}
