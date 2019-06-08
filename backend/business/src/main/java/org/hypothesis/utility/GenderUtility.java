/**
 * 
 */
package org.hypothesis.utility;

import org.hypothesis.data.api.Gender;
import org.hypothesis.server.Messages;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class GenderUtility {

	public static String getLocalizedName(String genderCode) {
		if (genderCode != null) {
			return getLocalizedName(Gender.get(genderCode));
		}

		return null;
	}

	public static String getLocalizedName(Gender gender) {
		if (gender != null) {
			return Messages.getString(gender.getMessageCode());
		}

		return null;
	}

}
