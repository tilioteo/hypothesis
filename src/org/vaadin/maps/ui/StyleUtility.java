/**
 * 
 */
package org.vaadin.maps.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import org.vaadin.maps.shared.ui.Style;

/**
 * @author kamil
 *
 */
public class StyleUtility {

	public static HashMap<String, String> getStyleMap(Style style) {
		if (style != null) {
			HashMap<String, String> map = new HashMap<String, String>();
			
			Field[] fields = style.getClass().getDeclaredFields();
			for (Field field : fields) {
				if ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC) {
					try {
						map.put(field.getName(), field.get(style).toString());
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			return map;
		}
		return null;
	}

}
