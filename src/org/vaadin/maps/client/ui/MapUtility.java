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

	
	public static final Style getStyleFromMap(Map<String, String> map, Style defaultStyle) {
		if (map != null && !map.isEmpty()) {
			Style style = new Style();

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
							int intValue = 0;
							if (!value.isEmpty()) {
								try {
									intValue = Integer.parseInt(value);
								} catch (NumberFormatException e) {}
							}
							field.setFieldValue(style, intValue);
						} else if (typeName.contains("double")) {
							double doubleValue = 0.0;
							if (!value.isEmpty()) {
								try {
									doubleValue = Double.parseDouble(value);
								} catch (NumberFormatException e) {}
							}
							field.setFieldValue(style, doubleValue);
						}
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
			return style;
		} else {
			return defaultStyle;
		}
	}
	
}
