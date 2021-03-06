/**
 * 
 */
package org.hypothesis.data;

import org.hypothesis.interfaces.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author morongk
 *
 */
public class XmlDocumentWriterTest {

	/**
	 * Test method for
	 * {@link org.hypothesis.data.XmlDocumentWriter#writeString(org.hypothesis.interfaces.Document)}
	 * .
	 */
	@Test
	public void testWriteString() {
		XmlDocumentReader reader = new XmlDocumentReader();
		XmlDocumentWriter writer = new XmlDocumentWriter();

		Document doc = reader.readString(XmlTestUtility.getSampleReaderXmlString());
		String txt = XmlTestUtility.getSampleWriterString();
		String str = writer.writeString(doc);

		assertEquals(txt, str);
	}

}
