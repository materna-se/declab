package de.materna.dmn.tester.beans.sessiontoken;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import de.materna.dmn.tester.beans.user.User;

@Entity
@Table(name = "sessiontoken", uniqueConstraints = @UniqueConstraint(columnNames = { "id" }))
public class SessionToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "user")
	@NotNull
	private UUID userUuid;
	@Column(name = "token", unique = true)
	@NotNull
	private String token;
	@Column(name = "initiation")
	private LocalDate initiation;
	@Column(name = "expiration")
	private LocalDate expiration;
	@Column(name = "update")
	private LocalDate lastUpdate;

	public SessionToken() {
	}

	public SessionToken(User user, String token) {
		this(user.getUuid(), token);
	}

	public SessionToken(UUID userUuid, String token) {
		this.userUuid = userUuid;
		this.token = token;
	}

	public Long getId() {
		return id;
	}

	public UUID getUserUuid() {
		return userUuid;
	}

	public String getToken() {
		return token;
	}

	public LocalDate getInitiation() {
		return initiation;
	}

	public LocalDate getExpiration() {
		return expiration;
	}

	public void setExpiration(LocalDate expiration) {
		this.expiration = expiration;
	}

	public LocalDate getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(LocalDate lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}