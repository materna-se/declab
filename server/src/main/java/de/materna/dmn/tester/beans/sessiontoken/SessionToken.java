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
		setInitiation(Timestamp.valueOf(LocalDateTime.now()));
		setExpiration(Timestamp.valueOf(addWorkdays(LocalDateTime.now(), 3)));
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

	public Timestamp getInitiation() {
		return initiation;
	}

	public void setInitiation(Timestamp initiation) {
		this.initiation = initiation;
	}

	public Timestamp getExpiration() {
		return expiration;
	}

	public void setExpiration(Timestamp expiration) {
		this.expiration = expiration;
	}

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public LocalDateTime addWorkdays(LocalDateTime date, int workdays) {
		if (workdays < 1) {
			return date;
		}

		LocalDateTime result = date;
		int addedDays = 0;
		while (addedDays != workdays) {
			result = workdays > 0 ? result.plusDays(1) : result.minusDays(1);
			if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY || result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
				addedDays = addedDays + (workdays > 0 ? 1 : -1);
			}
		}

		return result;
	}

	/**
	 * @param dayOfWeek    The day of week of the start day. The values are numbered
	 *                     following the ISO-8601 standard, from 1 (Monday) to 7
	 *                     (Sunday).
	 * @param businessDays The number of business days to count from the day of
	 *                     week. A negative number will count days in the past.
	 * 
	 * @return The absolute (positive) number of days including weekends.
	 */
	public long getAllDays(int dayOfWeek, long businessDays) {
		long result = 0;
		if (businessDays != 0) {
			boolean isStartOnWorkday = dayOfWeek < 6;
			long absBusinessDays = Math.abs(businessDays);

			if (isStartOnWorkday) {
				// if negative businessDays: count backwards by shifting weekday
				int shiftedWorkday = businessDays > 0 ? dayOfWeek : 6 - dayOfWeek;
				result = absBusinessDays + (absBusinessDays + shiftedWorkday - 1) / 5 * 2;
			} else { // start on weekend
				// if negative businessDays: count backwards by shifting weekday
				int shiftedWeekend = businessDays > 0 ? dayOfWeek : 13 - dayOfWeek;
				result = absBusinessDays + (absBusinessDays - 1) / 5 * 2 + (7 - shiftedWeekend);
			}
		}
		return result;
	}
}