/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.dom4j.Attribute;
import org.hypothesis.builder.DocumentImpl;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.Element;
import org.hypothesis.utility.XmlUtility;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
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
			e.printStackTrace();
		}

		return null;
	}

	private String composeName(org.dom4j.Element element) {
		String namespace = element.getNamespaceURI();
		
		if (!namespace.isEmpty()) {
			try {
				URL url = new URL(namespace);
				
				String[] parts = url.getHost().split("\\.");
				namespace = "";
				
				for (int i = parts.length-1; i >= 0; --i) {
					namespace += parts[i] + Document.NAMESPACE_SEPARATOR;
				}
				
				parts = url.getPath().split("/");
				for (int i = 0; i < parts.length; ++i) {
					if (!parts[i].isEmpty()) {
						namespace += parts[i] + Document.NAMESPACE_SEPARATOR;
					}
				}
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
				
			}
		}
		
		return namespace.isEmpty() ? element.getName() : namespace + element.getName();
	}

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

	private void copyAttributes(org.dom4j.Element xmlElement, Element element) {
		List<Attribute> xmlAttributes = xmlElement.attributes();

		for (Attribute xmlAttribute : xmlAttributes) {
			element.setAttribute(xmlAttribute.getName(), xmlAttribute.getValue());
		}
	}

}
