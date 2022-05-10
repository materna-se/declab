package de.materna.dmn.tester.beans.user;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.mindrot.jbcrypt.BCrypt;

@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
public class User {

	@Id
	@Column(name = "uuid", unique = true, nullable = false)
	private UUID uuid;
	@Column(name = "email", unique = true, nullable = false)
	@Email(message = "Email address must be valid")
	@NotEmpty(message = "E-Mail cannot be empty")
	private String email;
	@Column(name = "username", nullable = false)
	@NotEmpty(message = "Username cannot be empty")
	private String username;
	@Column(name = "firstname")
	private String firstname;
	@Column(name = "lastname")
	private String lastname;
	@Column(name = "password", nullable = false)
	@NotEmpty(message = "Password cannot be empty")
	private String password;
	@Column(name = "salt", nullable = false)
	private String salt;
	@Column(name = "registration")
	private LocalDateTime registrationDateTime;
	@Column(name = "systemadmin", columnDefinition = "boolean default false")
	private boolean systemAdmin;

	public User() {
	}

	public User(String email, String userName, String password, String firstname, String lastname) {
		this.uuid = UUID.randomUUID();
		this.registrationDateTime = LocalDateTime.now();
		this.salt = BCrypt.gensalt();
		setEmail(email);
		setUsername(userName);
		setPassword(BCrypt.hashpw(password, this.salt));
		setFirstname(firstname);
		setLastname(lastname);
		setSystemAdmin(false);
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public LocalDateTime getRegistrationDateTime() {
		return registrationDateTime;
	}

	public void setRegistrationDateTime(LocalDateTime registrationDateTime) {
		this.registrationDateTime = registrationDateTime;
	}

	public boolean isSystemAdmin() {
		return systemAdmin;
	}

	public void setSystemAdmin(boolean systemAdmin) {
		this.systemAdmin = systemAdmin;
	}
}