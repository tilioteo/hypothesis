/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.Element;
import org.hypothesis.utility.XmlUtility;

import com.tilioteo.common.Strings;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
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

		List<Element> elements = element.children();

		for (Element sourceElement : elements) {
			org.dom4j.Element destXmlElement = xmlElement.addElement(sourceElement.getName());
			if (!Strings.isNullOrEmpty(sourceElement.getText())) {
				destXmlElement.addText(sourceElement.getText());

				copyElement(sourceElement, destXmlElement);
			}
		}
	}

	private void copyAttributes(Element element, org.dom4j.Element xmlElement) {
		Map<String, String> attributes = element.attributes();

		for (Entry<String, String> entry : attributes.entrySet()) {
			xmlElement.addAttribute(entry.getKey(), entry.getValue());
		}
	}

}
