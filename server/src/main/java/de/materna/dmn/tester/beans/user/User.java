package de.materna.dmn.tester.beans.user;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
public class User {
	public static byte[] createHash(String password) {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
		try {
			return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).getEncoded();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Id
	@Column(name = "uuid", unique = true, nullable = false)
	private String uuid;
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
	@Column(name = "hash", nullable = false)
	@NotEmpty(message = "Password cannot be empty")
	private byte[] hash;
	@Column(name = "imageid")
	private String imageid;
	@Column(name = "registration")
	private LocalDateTime registrationDateTime;

	public User() {
	}

	public User(String email, String userName, String firstname, String lastname, String password) {
		this.uuid = UUID.randomUUID().toString();
		this.registrationDateTime = LocalDateTime.now();
		this.email = email;
		this.username = userName;
		this.firstname = firstname;
		this.lastname = lastname;
		this.hash = createHash(password);
		this.imageid = username + ".jpg";
	}

	public String getUuid() {
		return uuid;
	}

	public String getEmail() {
		return email;
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

	public byte[] getHash() {
		return hash;
	}

	public void setHash(byte[] hash) {
		this.hash = hash;
	}

	public String getImageid() {
		return imageid;
	}

	public void setImageid(String imageid) {
		this.imageid = imageid;
	}

	public LocalDateTime getRegistrationDateTime() {
		return registrationDateTime;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
