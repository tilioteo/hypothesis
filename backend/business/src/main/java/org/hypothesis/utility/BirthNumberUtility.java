package org.hypothesis.utility;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.valueOf;
import static java.time.LocalDate.now;
import static java.time.LocalDate.of;
import static java.time.Period.between;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import java.time.LocalDate;
import java.util.stream.Stream;

public class BirthNumberUtility {

	private static final int BIRTHNUM_MIN_LEN = 9;
	private static final int BIRTHNUM_MAX_LEN = 10;
	private static final int BIRTHNUM_BREAK = 53;

	/**
	 * Czech personal identification number aka "birth number" validation
	 * 
	 * @param birthNum
	 *            - Czech birth number
	 * @return
	 */
	public static boolean isValid(String birthNum) {
		// null check
		if (birthNum == null) {
			return false;
		}
		// removing slash
		birthNum = removeSlash(birthNum);
		// special kind of birth number
		if (isSpecial(birthNum)) {
			return true;
		}
		// length check
		if (birthNum.length() < BIRTHNUM_MIN_LEN || birthNum.length() > BIRTHNUM_MAX_LEN) {
			return false;
		}

		boolean isYearValid = isValidYear(birthNum);
		boolean isMonthValid = isValidMonth(birthNum);
		boolean isDayValid = isValidDay(birthNum);
		boolean isAfterSlashValid = isValidNumberAfterSlash(birthNum);

		return isYearValid && isMonthValid && isDayValid && isAfterSlashValid;
	}

	/**
	 * @return birth number without slash
	 */
	private static String removeSlash(String birthNum) {
		if (birthNum == null) {
			return null;
		}
		return birthNum.replaceAll("/", "");
	}

	/**
	 * Locates if given birth number is special <B>valid</B> case (auto
	 * generated). it must ends with 99 (HK method) or day field must be 00
	 * positions (as a day).
	 *
	 * @param birthNum
	 *            is birth number
	 * @return if <B>rc</B> is special case or not.
	 */
	public static boolean isSpecial(String birthNum) {
		if (birthNum != null) {
			final String numWithoutSlash = removeSlash(birthNum.trim());
			{
				/**
				 * Trojmístná koncovka byla přidělována do data narození 1. 1.
				 * 1954. Byly přidělovány i koncovky „000“. V tomto případě je
				 * čtvrté místo koncovky a poslední místo rodného čísla mezera.
				 * Kontrolní číslice je přidávána k RČ občanů narozených od 1.
				 * 1. 1954. Devítimístné číslo vytvořené z datové části před
				 * lomítkem a trojmístné koncovky dělíme 11 a celočíselný zbytek
				 * tohoto podílu je kontrolní číslice. Je-li zbytek nula, pak
				 * kontrolní číslice je nula.Je-li zbytek 10, pak kontrolní
				 * číslice je také 0 (podle interního předpisu FSÚ ČVK 2898/1985
				 * byly tyto nulové koncovky přidělovány pouze do roku 1985 v
				 * počtu cca 1000; není vyloučeno, že se v minimálním počtu
				 * vyskytly i po tomto roce).
				 */
				if (numWithoutSlash.length() == BIRTHNUM_MIN_LEN) {
					return parseLong(numWithoutSlash) % 11 == 10;
				} else if (numWithoutSlash.length() == BIRTHNUM_MAX_LEN) {
					/**
					 * Při max.délce RČ a poslední číslici 0(když se nejdná o
					 * náhradní RČ) se může jednat o RČ mezi lety 1954 - 1985 a
					 * může jít s speciíální případ viz FSÚ ČVK 2898/1985
					 */
					if ("0".equals(getLastChar(numWithoutSlash)) && !"00".equals(numWithoutSlash.substring(4, 6))) {
						Long birthNumNoLastChar = parseLong(numWithoutSlash.substring(0, numWithoutSlash.length() - 1));
						return birthNumNoLastChar % 11 == 10;
					}

					return "00".equals(numWithoutSlash.substring(4, 6));
				} else {
					return numWithoutSlash.length() >= 6 && "00".equals(numWithoutSlash.substring(4, 6));
				}
			}
		}
		return false;
	}

	/**
	 * Validace 1. a 2. číslice v českém rodném čísle podle Karla Toty <br>
	 * Řešeno v rámci https://iczbrno2.atlassian.net/browse/AMISHD-649
	 * 
	 * @param birthNum
	 * @return
	 */
	private static boolean isValidYear(String birthNum) {
		String year = birthNum.substring(0, 2);
		return isNumeric(year);
	}

	/**
	 * Validace 3. a 4. číslice v českém rodném čísle
	 * 
	 * @param birthNum
	 * @return
	 */
	private static boolean isValidMonth(String birthNum) {
		String month = birthNum.substring(2, 4);
		Integer monthInt = isNumeric(month) ? parseInt(month) : 0;
		if (!isNumeric(month)) {
			return false;
		} else if ((monthInt < 1) || (monthInt > 12 && monthInt < 21) || (monthInt > 32 && monthInt < 51)
				|| (monthInt > 62 && monthInt < 70)
				|| (monthInt > 82 && !Stream.of(87, 90, 99).anyMatch((i) -> i.equals(monthInt)))) {
			return false;
		}

		return true;
	}

	/**
	 * Validace 5. a 6. číslice v českém rodném čísle
	 * 
	 * @param birthNum
	 * @return
	 */
	private static boolean isValidDay(String birthNum) {
		String month = birthNum.substring(2, 4);
		String day = birthNum.substring(4, 6);

		Integer monthInt = isNumeric(month) ? parseInt(month) : 0;
		Integer dayInt = isNumeric(day) ? parseInt(day) : 0;

		if (isNumeric(day)) {
			dayInt = parseInt(day);
		}
		if (!isNumeric(day) || dayInt < 0) {
			return false;
		} else if (!Stream.of("70", "79", "87", "90", "99").anyMatch((i) -> i.equals(month))) {
			if ((dayInt > 29 && ("02".equals(month) || "52".equals(month)))
					|| (dayInt > 30 && Stream.of("04", "54", "06", "56", "09", "59", "11", "61")
							.anyMatch((i) -> i.equals(month)))
					|| (dayInt > 31 && Stream
							.of("01", "51", "03", "53", "05", "55", "07", "57", "08", "58", "10", "60", "12", "62")
							.anyMatch((i) -> i.equals(month)))
					|| ((dayInt < 51 || dayInt > 79) && (monthInt == 22 || monthInt == 72))
					|| ((dayInt < 51 || dayInt > 80) && Stream.of("24", "74", "26", "76", "29", "79", "31", "81")
							.anyMatch((i) -> i.equals(month)))
					|| ((dayInt < 51 || dayInt > 81) && Stream
							.of("21", "71", "23", "73", "25", "75", "27", "77", "28", "78", "30", "80", "32", "82")
							.anyMatch((i) -> i.equals(month)))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Validace čísel za lomítkem v českém rodném čísle
	 * 
	 * @param birthNum
	 * @return
	 */
	private static boolean isValidNumberAfterSlash(String birthNum) {
		String year = birthNum.substring(0, 2);
		String month = birthNum.substring(2, 4);
		String day = birthNum.substring(4, 6);

		Integer yearInt = isNumeric(year) ? parseInt(year) : 0;
		String lastNum = birthNum.length() == BIRTHNUM_MAX_LEN ? getLastChar(birthNum) : " ";

		if (!isNumeric(lastNum) && !" ".equals(lastNum)) {
			return false;
		} else if (" ".equals(lastNum)) {
			if (Stream.of("70", "79", "87", "90", "99", "00").anyMatch((i) -> i.equals(month))) {
				return false;
			} else if (yearInt >= 54 && getAge(birthNum) < 94) {
				return false;
			}

		} else if (!day.equals("00")) {
			if (!isModulo11(birthNum)) {
				return false;
			}
		}

		// kontrola zbývajících znaků za lomítkem
		if ((!isNumeric(valueOf(birthNum.charAt(6)))) || (!isNumeric(valueOf(birthNum.charAt(7))))
				|| (!isNumeric(valueOf(birthNum.charAt(8))))) {
			return false;
		}

		return true;
	}

	/**
	 * @return true for BIRTH_NUM % 11 Special case is "special" birth number
	 *         which is needn't be modulo 11, but checking must return true
	 */
	private static boolean isModulo11(String birthNum) {
		if (isSpecial(birthNum)) {
			return true;
		}
		try {
			final long bn = parseLong(birthNum.substring(0, 10));
			return (bn % 11 == 0);
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Gets last character of czech birth number only if its length equals
	 * min/max length
	 * 
	 * @param birthNum
	 * @return
	 */
	private static String getLastChar(String birthNum) {
		return birthNum.substring(birthNum.length() - 1);
	}

	/**
	 * Computes the age from birth number and current time. If personal number
	 * doesn't contain correct date of birth, -1 is returned
	 *
	 * @return the age computed from birth number.
	 */
	public static int getAge(String birthNum) {
		final LocalDate birthDate = parseDate(birthNum);
		if (birthDate != null) {
			return between(birthDate, now()).getYears();
		}

		return -1;
	}

	/**
	 * Parse date from prepared birth number.
	 *
	 * @param birthNum
	 *            - birth number of valid length (9 or 10 digits). Year is
	 *            determinated to four digit number by length of "slash" part -
	 *            only three digits mean year <= 1953, four digits, year >=
	 *            1954.<BR>
	 *            Special birth number (has two zeros at day place), generate 1
	 *            as a day.
	 * @return appropriate date by given birth number or <B>null</B>, if date
	 *         part of birth number is wrong.
	 */
	public static LocalDate parseDate(final String birthNum) {
		try {
			final String birthNumWithoutSlash = removeSlash(birthNum);
			int year = parseInt(birthNumWithoutSlash.substring(0, 2));
			int month = parseInt(birthNumWithoutSlash.substring(2, 4));
			// in special birth number 1 is choosed as a valid day of month
			int day = isSpecial(birthNumWithoutSlash) ? 1 : parseInt(birthNumWithoutSlash.substring(4, 6));

			// month correction
			if (month > 12) {
				if (month > 20 && month <= 32) { // Tento den bylo na jedné
													// matrice přiděleno více
													// rodných čísel pro chlapce
					month -= 20;
				} else if (month > 50 && month <= 62) { // Ženy
					month = month - 50;
				} else if (month > 70 && month <= 82) { // Tento den bylo na
														// jedné matrice
														// přiděleno více
														// rodných čísel pro
														// dívky
					month -= 70;
				} else {
					return null;
				}
			}

			// U cizinců, kterým je přiděleno RČ je rozvněž potřeba udělat
			// korekci (dříve začínalo lomítko číslicemi 99)
			if (day > 50 && day <= 81) {
				day -= 50;
			}

			// year correction
			if (birthNumWithoutSlash.length() == BIRTHNUM_MIN_LEN) {
				year += (year > BIRTHNUM_BREAK) ? 1800 : 1900;
				// vlozeno navic
				if (year <= 1890) {
					year = year + 100;
				}
			} else {
				year += (year > BIRTHNUM_BREAK) ? 1900 : 2000;
			}

			return of(year, month, day);
		} catch (RuntimeException e) {
			return null;
		}
	}

	/**
	 * Returns sex char based on birth number
	 *
	 * @param birthNum
	 * @return 'M' for male, 'F' for female or 'U' for unknown sex
	 */
	public static char getSexChar(String birthNum) {
		char sex = 'U';
		if (birthNum != null && birthNum.length() >= 3) {
			final char ch = birthNum.charAt(2);
			if (ch == '0' || ch == '1') {
				sex = 'M';
			}
			if (ch == '5' || ch == '6') {
				sex = 'F';
			}
		}
		return sex;
	}

}
