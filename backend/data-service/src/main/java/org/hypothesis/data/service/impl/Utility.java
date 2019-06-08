package org.hypothesis.data.service.impl;

import java.util.Date;

class Utility {

	public static Date longToDate(Long timestamp) {
		return timestamp != null ? new Date(timestamp) : null;
	}

	public static Long dateToLong(Date date) {
		return date != null ? date.getTime() : null;
	}

}
