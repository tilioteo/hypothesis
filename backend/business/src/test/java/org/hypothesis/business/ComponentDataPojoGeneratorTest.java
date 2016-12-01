/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import org.hypothesis.event.annotations.ElementPath;
import org.hypothesis.event.data.ComponentData;
import org.hypothesis.utility.ReflectionUtility;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class ComponentDataPojoGeneratorTest {

	/**
	 * Test method for
	 * {@link org.hypothesis.business.ComponentDataPojoGenerator#generate(java.lang.String, java.lang.Class, java.util.Map, java.util.Map)}
	 * .
	 */
	@Test
	public void testGenerate() {
		Map<String, Class<?>> properties = new HashMap<>();
		Map<String, Annotation> annotations = new HashMap<>();

		properties.put("stringValue", String.class);
		properties.put("integerValue", Integer.class);
		properties.put("objectValue", Object.class);

		annotations.put("integerValue", new ElementPath() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return ElementPath.class;
			}

			@Override
			public String value() {
				return "selected/index";
			}
		});

		try {
			Class<?> clazz = ComponentDataPojoGenerator.generate(ComponentData.class.getName() + "$Generated",
					ComponentData.class, properties, annotations);

			ComponentData instance = (ComponentData) clazz.newInstance();

			Field field = ReflectionUtility.getDeclaredField(instance, "id");
			field.setAccessible(true);
			field.set(instance, "id-string");

			assertTrue("id-string".equals(instance.getId()));

			field = ReflectionUtility.getDeclaredField(instance, "integerValue");
			field.setAccessible(true);
			field.set(instance, 123);

			assertTrue(123 == (int) field.get(instance));

			ElementPath structured = field.getAnnotation(ElementPath.class);

			assertNotNull(structured);

		} catch (Throwable e) {
			fail("exception thrown");
			e.printStackTrace();
		}

	}

}
