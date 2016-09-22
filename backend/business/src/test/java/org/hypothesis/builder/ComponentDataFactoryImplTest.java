/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import static org.junit.Assert.assertEquals;

import org.hypothesis.data.DocumentWriter;
import org.hypothesis.data.XmlDocumentWriter;
import org.hypothesis.event.data.ComponentData;
import org.junit.Test;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class ComponentDataFactoryImplTest {

	/**
	 * Test method for
	 * {@link org.hypothesis.builder.ComponentDataFactoryImpl#buildComponentData(org.hypothesis.event.data.ComponentData, org.hypothesis.data.DocumentWriter)}
	 * .
	 */
	@Test
	public void testBuildComponentData() {
		ComponentDataFactory factory = new ComponentDataFactoryImpl();
		DocumentWriter writer = new XmlDocumentWriter();

		ComponentData data = ComponentDataTestUtility.createTestComponentData("testid", "TEST_TYPE", "TEST_EVENT");

		String result = factory.buildComponentData(data, writer);

		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<EventData><Source Type=\"TEST_TYPE\" Id=\"testid\" Name=\"TEST_EVENT\">BUTTON<Selected><Value Index=\"101\">somestringvalue</Value></Selected></Source></EventData>",
				result);
	}

	/**
	 * Test method for
	 * {@link org.hypothesis.builder.ComponentDataFactoryImpl#buildActionData(org.hypothesis.event.model.ActionEvent, org.hypothesis.data.DocumentWriter)}
	 * .
	 */
	@Test
	public void testBuildActionData() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.hypothesis.builder.ComponentDataFactoryImpl#buildSlideContainerData(org.hypothesis.interfaces.SlidePresenter, org.hypothesis.data.DocumentWriter)}
	 * .
	 */
	@Test
	public void testBuildSlideContainerData() {
		// fail("Not yet implemented");
	}

}
