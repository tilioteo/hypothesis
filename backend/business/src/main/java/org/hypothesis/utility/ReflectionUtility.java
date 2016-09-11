/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.utility;

import java.lang.reflect.Field;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class ReflectionUtility {

	private ReflectionUtility() {
	}

	/**
	 * Get field object from class instance including inherited fields
	 * 
	 * @param obj
	 *            instance of class
	 * @param name
	 *            field name
	 * @return Field definition of class
	 */
	public static Field getDeclaredField(Object obj, String name) {
		if (obj != null && name != null && !name.trim().isEmpty()) {
			Field field = null;

			Class<?> clazz = obj.getClass();

			while (clazz != null && null == field) {
				try {
					field = clazz.getDeclaredField(name);
				} catch (Exception e) {
				}

				clazz = clazz.getSuperclass();
			}

			return field;
		}

		return null;
	}

}
