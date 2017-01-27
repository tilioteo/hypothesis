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
public class XmlDocumentReaderTest {

	/**
	 * Test method for {@link org.hypothesis.data.XmlDocumentReader#readString(java.lang.String)}.
	 */
	@Test
	public void testReadString() {
		
		XmlDocumentReader reader = new XmlDocumentReader();
		
		String txt = XmlTestUtility.getSampleReaderString();
		System.out.println(txt);
		Document doc = reader.readString(XmlTestUtility.getSampleReaderXmlString());
		
		String str = doc.toString();
		System.out.println(str);
		assertEquals(txt, str);
	}

}
