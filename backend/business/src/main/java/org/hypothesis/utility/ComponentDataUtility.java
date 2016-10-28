package org.hypothesis.utility;
/**
 * 
 */

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.hypothesis.event.data.ComponentData;

/**
 * @author Kamil Morong
 *
 */
public class ComponentDataUtility {

	public static void setComponentDataPropertyValue(ComponentData data, String name, Object value) {
		if (data != null && StringUtils.isNotEmpty(name)) {
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
