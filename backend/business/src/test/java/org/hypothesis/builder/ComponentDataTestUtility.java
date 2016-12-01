/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import com.vaadin.ui.Button;
import org.hypothesis.business.ComponentDataPojoGenerator;
import org.hypothesis.event.annotations.ElementPath;
import org.hypothesis.event.data.ComponentData;
import org.hypothesis.event.data.ComponentDataConstants;
import org.hypothesis.utility.ComponentDataUtility;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public final class ComponentDataTestUtility {

	private ComponentDataTestUtility() {
	}

	public static ComponentData createTestComponentData(String id, String typeName, String eventName) {
		Map<String, Class<?>> properties = new HashMap<>();
		Map<String, Annotation> annotations = new HashMap<>();

		properties.put("stringValue", String.class);
		properties.put("integerValue", Integer.class);
		properties.put("objectValue", Object.class);

		annotations.put("stringValue", new ElementPath() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return ElementPath.class;
			}

			@Override
			public String value() {
				return "selected/value";
			}
		});

		annotations.put("integerValue", new ElementPath() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return ElementPath.class;
			}

			@Override
			public String value() {
				return "selected/value@index";
			}
		});

		try {
			Class<?> clazz = ComponentDataPojoGenerator.generate(ComponentData.class.getName() + "$Generated",
					ComponentData.class, properties, annotations);

			ComponentData data = (ComponentData) clazz.newInstance();

			ComponentDataUtility.setComponentDataPropertyValue(data, ComponentDataConstants.PROP_SENDER,
					new Button("BUTTON"));
			ComponentDataUtility.setComponentDataPropertyValue(data, ComponentDataConstants.PROP_ID, id);
			ComponentDataUtility.setComponentDataPropertyValue(data, ComponentDataConstants.PROP_TYPE_NAME, typeName);
			ComponentDataUtility.setComponentDataPropertyValue(data, ComponentDataConstants.PROP_EVENT_NAME, eventName);

			ComponentDataUtility.setComponentDataPropertyValue(data, "stringValue", "somestringvalue");
			ComponentDataUtility.setComponentDataPropertyValue(data, "integerValue", 101);
			ComponentDataUtility.setComponentDataPropertyValue(data, "objectValue", new Object());

			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
