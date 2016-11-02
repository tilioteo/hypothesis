package org.hypothesis.utility;
/**
 * 
 */

import org.apache.commons.lang3.StringUtils;
import org.hypothesis.event.data.ComponentData;

import java.lang.reflect.Field;

/**
 * @author Kamil Morong
 *
 */
public final class ComponentDataUtility {

	private ComponentDataUtility() {
	}

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
