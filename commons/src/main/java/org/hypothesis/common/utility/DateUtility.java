/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.common.utility;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class DateUtility {

	public static Date removeTime(Date date) {
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		}
		return null;
	}
}
