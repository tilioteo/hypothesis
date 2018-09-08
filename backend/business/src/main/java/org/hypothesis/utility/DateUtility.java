package org.hypothesis.utility;

import static java.time.Instant.ofEpochMilli;
import static java.time.ZoneId.systemDefault;
import static java.util.Date.from;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class DateUtility {

	public static Date toDate(final LocalDate date) {
		if (date != null) {
			return new Date(java.sql.Date.valueOf(date).getTime());
		}

		return null;
	}

	public static Date toDate(final LocalTime time) {
		if (time != null) {
			return new Date(java.sql.Time.valueOf(time).getTime());
		}

		return null;
	}

	public static LocalDate toLocalDate(final Date date) {
		if (date != null) {
			return toLocalDateTime(date).toLocalDate();
		}

		return null;
	}

	public static Date toDateWithTime(LocalDateTime dateTime) {
		if (dateTime != null) {
			return from(dateTime.atZone(systemDefault()).toInstant());
		}

		return null;
	}

	public static LocalDateTime toLocalDateTime(final Date date) {
		if (date != null) {
			return ofEpochMilli(date.getTime()).atZone(systemDefault()).toLocalDateTime();
		}

		return null;
	}

}
