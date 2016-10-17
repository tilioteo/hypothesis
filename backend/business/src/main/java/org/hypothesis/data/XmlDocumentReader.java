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
 *         Reader implementation for XML structured data
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
		} catch (Exception e) {
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

				StringBuilder namespaceBuilder = new StringBuilder();

				for (int i = parts.length - 1; i >= 0; --i) {
					namespaceBuilder.append(parts[i]);
					namespaceBuilder.append(Document.NAMESPACE_SEPARATOR);
				}

				parts = url.getPath().split("/");
				for (int i = 0; i < parts.length; ++i) {
					if (!parts[i].isEmpty()) {
						namespaceBuilder.append(parts[i]);
						namespaceBuilder.append(Document.NAMESPACE_SEPARATOR);
					}
				}

				namespaceBuilder.append(element.getName());

				return namespaceBuilder.toString();

			} catch (MalformedURLException e) {
				e.printStackTrace();

			}
		}

		return element.getName();
	}

	@SuppressWarnings("unchecked")
	private void copyElement(org.dom4j.Element xmlElement, Element element) {
		copyAttributes(xmlElement, element);

		((List<org.dom4j.Element>) xmlElement.elements()).forEach(e -> {
			String text;
			if (e.isTextOnly()) {
				text = e.getText();
			} else {
				text = e.getTextTrim();
			}

			copyElement(e, element.createChild(composeName(e), text));
		});
	}

	@SuppressWarnings("unchecked")
	private void copyAttributes(org.dom4j.Element xmlElement, Element element) {
		((List<Attribute>) xmlElement.attributes()).forEach(e -> element.setAttribute(e.getName(), e.getValue()));
	}

}
