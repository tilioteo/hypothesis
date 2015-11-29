/**
 * 
 */
package com.tilioteo.hypothesis.utility;

import java.lang.reflect.Field;

/**
 * @author kamil
 *
 */
public class ReflectionUtility {

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
				} catch (Throwable e) {
				}

				clazz = clazz.getSuperclass();
			}

			return field;
		}

		return null;
	}

	/*public static Field[] getDeclaredFields(Object obj) {
		if (obj != null) {
			Field[] fields = obj.getClass().getd
		}
		
		return null;
	}*/
}
