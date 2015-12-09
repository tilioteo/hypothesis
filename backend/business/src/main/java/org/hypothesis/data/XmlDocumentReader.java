/**
 * 
 */
package org.hypothesis.data;

import java.util.List;

import org.dom4j.Attribute;
import org.hypothesis.builder.DocumentImpl;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.Element;
import org.hypothesis.utility.XmlUtility;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class XmlDocumentReader implements DocumentReader {

	@Override
	public Document readString(String string) {
		try {
			org.dom4j.Document xmlDocument = XmlUtility.readString(string);
			if (xmlDocument != null) {
				xmlDocument.normalize();
				DocumentImpl document = new DocumentImpl();

				org.dom4j.Element xmlRoot = xmlDocument.getRootElement();
				Element root = document.createRoot(composeName(xmlRoot));

				copyElement(xmlRoot, root);

				return document;
			}
		} catch (Throwable e) {
		}

		return null;
	}

	private String composeName(org.dom4j.Element element) {
		String namespace = element.getNamespaceURI();
		if (!namespace.isEmpty() && !namespace.endsWith(DocumentImpl.NAMESPACE_SEPARATOR)) {
			namespace += DocumentImpl.NAMESPACE_SEPARATOR;
		}
		return namespace.isEmpty() ? element.getName() : namespace + element.getName();
	}

	@SuppressWarnings("unchecked")
	private void copyElement(org.dom4j.Element xmlElement, Element element) {
		copyAttributes(xmlElement, element);

		List<org.dom4j.Element> xmlElements = xmlElement.elements();

		for (org.dom4j.Element xmlSourceElement : xmlElements) {
			String text;
			if (xmlSourceElement.isTextOnly()) {
				text = xmlSourceElement.getText();
			} else {
				text = xmlSourceElement.getTextTrim();
			}

			Element destElement = element.createChild(composeName(xmlSourceElement), text);

			copyElement(xmlSourceElement, destElement);
		}
	}

	@SuppressWarnings("unchecked")
	private void copyAttributes(org.dom4j.Element xmlElement, Element element) {
		List<Attribute> xmlAttributes = xmlElement.attributes();

		for (Attribute xmlAttribute : xmlAttributes) {
			element.setAttribute(xmlAttribute.getName(), xmlAttribute.getValue());
		}
	}

}
