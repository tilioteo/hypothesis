package org.hypothesis.utility;
/**
 * 
 */

import java.lang.reflect.Field;

import org.hypothesis.event.data.ComponentData;

import com.tilioteo.common.Strings;

/**
 * @author Kamil Morong
 *
 */
public class ComponentDataUtility {

	public static void setComponentDataPropertyValue(ComponentData data, String name, Object value) {
		if (data != null && !Strings.isNullOrEmpty(name)) {
			Field field = ReflectionUtility.getDeclaredField(data, name);
			if (field != null) {
				field.setAccessible(true);
				try {
					field.set(data, value);
				} catch (Exception e) {
				}
			}
		}

	}

}
