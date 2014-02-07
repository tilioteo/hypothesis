/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.vaadin.gwt.i18n.client;

public class DecimalFormatSymbols {

	private static boolean nullEquals(Object obj1, Object obj2) {
		return obj1 == null ? obj2 == null : obj1.equals(obj2);
	}

	private static int nullHash(Object obj) {
		return obj == null ? 0 : obj.hashCode();
	}

	private char decimalSeparator = '.';
	private char digit = '#';
	private String exponentSeparator = "E";
	private char groupingSeparator = ',';
	private String infinity = "\u221E";
	private char minusSign = '-';
	private String nan = "NaN";
	private char patternSeparator = ';';
	private char percent = '%';
	private char perMill = '\u2030';

	private char zeroDigit = '0';

	public DecimalFormatSymbols() {
	}
 
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof DecimalFormatSymbols)) {
			return false;
		}
		DecimalFormatSymbols dfs = (DecimalFormatSymbols) other;
		return decimalSeparator == dfs.decimalSeparator
				&& digit == dfs.digit
				&& nullEquals(exponentSeparator, dfs.exponentSeparator)
				&& groupingSeparator == dfs.groupingSeparator
				&& nullEquals(infinity, dfs.infinity)
				&& minusSign == dfs.minusSign
				&& nullEquals(nan, dfs.nan)
				&& patternSeparator == dfs.patternSeparator
				&& percent == dfs.percent
				&& perMill == dfs.perMill
				&& zeroDigit == dfs.zeroDigit;
	}

	/**
	 * @return the decimalSeparator
	 */
	public char getDecimalSeparator() {
		return decimalSeparator;
	}

	/**
	 * @return the digit
	 */
	public char getDigit() {
		return digit;
	}

	/**
	 * @return the exponentSeparator
	 */
	public String getExponentSeparator() {
		return exponentSeparator;
	}

	/**
	 * @return the groupingSeparator
	 */
	public char getGroupingSeparator() {
		return groupingSeparator;
	}

	/**
	 * @return the infinity
	 */
	public String getInfinity() {
		return infinity;
	}

	/**
	 * @return the minusSign
	 */
	public char getMinusSign() {
		return minusSign;
	}

	/**
	 * @return the naN
	 */
	public String getNaN() {
		return nan;
	}

	/**
	 * @return the patternSeparator
	 */
	public char getPatternSeparator() {
		return patternSeparator;
	}

	/**
	 * @return the percent
	 */
	public char getPercent() {
		return percent;
	}

	/**
	 * @return the perMill
	 */
	public char getPerMill() {
		return perMill;
	}

	/**
	 * @return the zeroDigit
	 */
	public char getZeroDigit() {
		return zeroDigit;
	}

	@Override
	public int hashCode() {
		return decimalSeparator	* 7 + digit * 11 + nullHash(exponentSeparator) * 13
				+ groupingSeparator * 17 + nullHash(infinity) * 19 + minusSign
				* 23 + nullHash(nan) * 31
				+ patternSeparator * 37 + percent * 41 + perMill * 43
				+ zeroDigit * 53;
	}

}
