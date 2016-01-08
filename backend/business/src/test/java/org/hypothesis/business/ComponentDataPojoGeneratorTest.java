/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.hypothesis.business.ComponentDataPojoGenerator;
import org.hypothesis.business.Structured;
import org.hypothesis.event.data.ComponentData;
import org.hypothesis.utility.ReflectionUtility;
import org.junit.Test;

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
		HashMap<String, Class<?>> properties = new HashMap<>();
		HashMap<String, String> structures = new HashMap<>();

		properties.put("stringValue", String.class);
		properties.put("integerValue", Integer.class);
		properties.put("objectValue", Object.class);

		structures.put("integerValue", "selected/index");

		try {
			Class<?> clazz = ComponentDataPojoGenerator.generate(ComponentData.class.getName() + "$Generated",
					ComponentData.class, properties, structures);

			ComponentData instance = (ComponentData) clazz.newInstance();

			Field field = ReflectionUtility.getDeclaredField(instance, "id");
			field.setAccessible(true);
			field.set(instance, "id-string");

			assertTrue("id-string".equals(instance.getId()));

			field = ReflectionUtility.getDeclaredField(instance, "integerValue");
			field.setAccessible(true);
			field.set(instance, 123);

			assertTrue(123 == (int) field.get(instance));

			Structured structured = field.getAnnotation(Structured.class);

			assertNotNull(structured);

		} catch (Throwable e) {
			fail("exception thrown");
			e.printStackTrace();
		}

	}

}