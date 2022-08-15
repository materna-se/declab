package de.materna.dmn.tester.beans.sessiontoken;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import de.materna.dmn.tester.beans.user.User;

@Entity
@Table(name = "`sessiontoken`", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
public class SessionToken {
	@Id
	@Column(name = "uuid", unique = true, nullable = false)
	private String uuid;
	@Column(name = "userUuid")
	@NotNull
	private String userUuid;
	@Column(name = "initiation")
	private Timestamp initiation;
	@Column(name = "expiration")
	private Timestamp expiration;
	@Column(name = "update")
	private Timestamp lastUpdate;

	public SessionToken() {
	}

	public SessionToken(User user) {
		this(user.getUuid());
	}

	public SessionToken(String userUuid) {
		this.uuid = UUID.randomUUID().toString();
		setUserUuid(userUuid);
		setInitiation(LocalDateTime.now());
		setExpiration(addWorkdays(getInitiation(), 3));
		setLastUpdate(getInitiation());
	}

	public String getUuid() {
		return uuid;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public LocalDateTime getInitiation() {
		return initiation.toLocalDateTime();
	}

	public void setInitiation(LocalDateTime initiation) {
		this.initiation = Timestamp.valueOf(initiation);
	}

	public LocalDateTime getExpiration() {
		return expiration.toLocalDateTime();
	}

	public void setExpiration(LocalDateTime expiration) {
		this.expiration = Timestamp.valueOf(expiration);
	}

	public LocalDateTime getLastUpdate() {
		return lastUpdate.toLocalDateTime();
	}

	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = Timestamp.valueOf(lastUpdate);
	}

	public static LocalDateTime addWorkdays(LocalDateTime dateTime, int workdays) {
		if (workdays != 0) {
		int addedDays = 0;
		while (addedDays != workdays) {
				dateTime = dateTime.plusDays(workdays > 0 ? 1 : -1);
				if (!(dateTime.getDayOfWeek() == DayOfWeek.SATURDAY || dateTime.getDayOfWeek() == DayOfWeek.SUNDAY)) {
					addedDays += workdays > 0 ? 1 : -1;
			}
		}
		}
		return dateTime;
	}
}