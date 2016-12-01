/**
 * 
 */
package org.hypothesis.data;

import static org.junit.Assert.*;

import org.hypothesis.interfaces.Document;
import org.junit.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;

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

		String orig = XmlTestUtility.getSampleReaderXmlString();
		Document doc = reader.readString(orig);
		String txt = XmlTestUtility.getSampleWriterString();
		String str = writer.writeString(doc);

		/*
		//@formatter:off
		Diff similar = DiffBuilder.compare(orig).withTest(str)
				.withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
				.ignoreComments()
				.normalizeWhitespace()
				.checkForSimilar()
				.build();
		Diff identical = DiffBuilder.compare(orig).withTest(str)
				.withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
				.ignoreComments()
				.normalizeWhitespace()
				.checkForIdentical()
				.build();
		//@formatter:on

		assertFalse("XML similar " + similar.toString(), similar.hasDifferences());
		assertFalse("XML identical " + identical.toString(), identical.hasDifferences());
		*/

		assertEquals(txt, str);
	}

}
