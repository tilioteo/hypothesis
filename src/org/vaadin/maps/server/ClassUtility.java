/**
 * 
 */
package org.vaadin.maps.server;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author kamil
 *
 */
public class ClassUtility {
	
	public static Class<?> getGenericTypeClass(Class<?> clazz, int index) {
		Class<?> superClass = clazz.getSuperclass();
		Type genericSuperClass = clazz.getGenericSuperclass();
		while (!(genericSuperClass instanceof ParameterizedType)) {
			genericSuperClass = superClass.getGenericSuperclass();
			superClass = superClass.getSuperclass();
		}

		Type[] types = ((ParameterizedType) genericSuperClass).getActualTypeArguments();
		if (index < types.length) {
			return (Class<?>) types[index];
		} else {
			return null;
		}
	}

}
