package de.materna.dmn.tester.beans.sessiontoken;

import java.time.LocalDateTime;

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
	private String userUuid;
	@Column(name = "token", unique = true)
	@NotNull
	private String token;
	@Column(name = "initiation")
	private LocalDateTime initiation;
	@Column(name = "expiration")
	private LocalDateTime expiration;
	@Column(name = "update")
	private LocalDateTime lastUpdate;

	public SessionToken() {
	}

	public SessionToken(User user, String token) {
		this(user.getUuid(), token);
	}

	public SessionToken(String userUuid, String token) {
		this.userUuid = userUuid;
		this.token = token;
	}

	public Long getId() {
		return id;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public String getToken() {
		return token;
	}

	public LocalDateTime getInitiation() {
		return initiation;
	}

	public LocalDateTime getExpiration() {
		return expiration;
	}

	public void setExpiration(LocalDateTime expiration) {
		this.expiration = expiration;
	}

	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}