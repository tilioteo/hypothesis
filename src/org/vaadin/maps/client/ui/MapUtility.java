/**
 * 
 */
package org.vaadin.maps.client.ui;

import java.util.Map;

import org.vaadin.maps.shared.ui.Style;

import com.gwtent.reflection.client.ClassType;
import com.gwtent.reflection.client.Field;
import com.gwtent.reflection.client.TypeOracle;

/**
 * @author kamil
 *
 */
public class MapUtility {

	
	public static final Style getStyleFromMap(Map<String, String> map) {
		Style style = new Style();
		
		if (map != null) {
			ClassType<Style> classType = TypeOracle.Instance.getClassType(Style.class);

			Field[] fields = classType.getFields();
			for (Field field : fields) {
				String name = field.getName();
				if (map.containsKey(name)) {
					try {
						String typeName = field.getType().getSimpleSourceName().toLowerCase();
						String value = map.get(name);
						if (typeName.contains("string")) {
							field.setFieldValue(style, value);
						} else if (typeName.contains("int")) {
							if (value.isEmpty()) {
								field.setFieldValue(style, 0);
							} else {
								field.setFieldValue(style, Integer.parseInt(value));
							}
						} else if (typeName.contains("double")) {
							if (value.isEmpty()) {
								field.setFieldValue(style, 0.0);
							} else {
								field.setFieldValue(style, Double.parseDouble(value));
							}
						}
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
		}
		
		return style;
	}
	
}
