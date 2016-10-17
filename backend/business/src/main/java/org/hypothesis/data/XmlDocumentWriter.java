/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data;

import org.apache.commons.lang3.StringUtils;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.Element;
import org.hypothesis.utility.XmlUtility;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 *         Writer implementation for XML structured data
 */
@SuppressWarnings("serial")
public class XmlDocumentWriter implements DocumentWriter {

	@Override
	public String writeString(Document document) {

		org.dom4j.Document xmlDocument = XmlUtility.createDocument();

		Element root = document.root();
		org.dom4j.Element xmlRoot = xmlDocument.addElement(root.getName());

		copyElement(root, xmlRoot);

		xmlDocument.normalize();
		return XmlUtility.writeString(xmlDocument);
	}

	private void copyElement(Element element, org.dom4j.Element xmlElement) {
		copyAttributes(element, xmlElement);

		element.children().forEach(e -> {
			org.dom4j.Element destXmlElement = xmlElement.addElement(e.getName());
			if (StringUtils.isNotEmpty(e.getText())) {
				destXmlElement.addText(e.getText());
			}
			copyElement(e, destXmlElement);
		});
	}

	private void copyAttributes(Element element, org.dom4j.Element xmlElement) {
		element.attributes().entrySet().forEach(e -> xmlElement.addAttribute(e.getKey(), e.getValue()));
	}

}
