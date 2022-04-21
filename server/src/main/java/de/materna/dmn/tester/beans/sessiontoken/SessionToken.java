package de.materna.dmn.tester.beans.sessiontoken;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import de.materna.dmn.tester.beans.user.User;

@Entity
@Table(name = "sessiontoken", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
public class SessionToken {
	@Id
	@Column(name = "uuid", unique = true, nullable = false)
	private UUID uuid;
	@Column(name = "user")
	@NotNull
	private UUID userUuid;
	@Column(name = "initiation")
	private LocalDate initiation;
	@Column(name = "expiration")
	private LocalDate expiration;
	@Column(name = "update")
	private LocalDate lastUpdate;

	public SessionToken() {
	}

	public SessionToken(User user) {
		this(user.getUuid());
	}

	public SessionToken(UUID userUuid) {
		this.uuid = UUID.randomUUID();
		setUserUuid(userUuid);
	}

	public UUID getUuid() {
		return uuid;
	}

	public UUID getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(UUID userUuid) {
		this.userUuid = userUuid;
	}

	public LocalDate getInitiation() {
		return initiation;
	}

	public void setInitiation(LocalDate initiation) {
		this.initiation = initiation;
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