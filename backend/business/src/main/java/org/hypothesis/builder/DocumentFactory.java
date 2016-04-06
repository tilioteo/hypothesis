/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hypothesis.common.utility.DocumentUtility;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;

import com.tilioteo.common.Strings;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class DocumentFactory {

	public static Document mergeSlideDocument(Document template, Document content) {
		DocumentImpl document = new DocumentImpl();
		Element root = document.createRoot(DocumentConstants.SLIDE);

		// copy template
		List<Element> templateElements = template.root().children();
		for (Element element : templateElements) {
			root.createChild(element);
		}

		// bind slide content
		Element element = content.root().selectElement(DocumentConstants.BINDINGS);
		List<Element> elements = element.selectElements(DocumentConstants.BIND);

		for (Element bindElement : elements) {
			Element bindContent = bindElement.firstChild();

			if (bindContent != null) {
				String name = bindContent.getName();
				String id = bindContent.getAttribute(DocumentConstants.ID);

				if (!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(id)) {
					HashMap<String, String> attributes = new HashMap<>();
					attributes.put(DocumentConstants.ID, id);

					Element origElement = DocumentUtility.findElementByNameAndValue(root, name, attributes, true);
					if (origElement != null) {
						mergeElementAttributes(origElement, bindContent);
						List<Element> bindNodes = bindContent.children();

						for (Element bindNode : bindNodes) {
							mergeBindingNodes(origElement, bindNode);
						}
					}
				}
			}
		}

		return document;
	}

	private static void mergeElementAttributes(Element destination, Element source) {
		Map<String, String> sourceAttributes = source.attributes();
		for (Entry<String, String> entry : sourceAttributes.entrySet()) {
			destination.setAttribute(entry.getKey(), entry.getValue());
		}
	}

	private static void mergeBindingNodes(Element destinationElement, Element sourceSubElement) {
		String name = sourceSubElement.getName();
		String id = sourceSubElement.getAttribute(DocumentConstants.ID);

		Element destinationSubElement = null;
		if (!Strings.isNullOrEmpty(id)) {
			HashMap<String, String> attributes = new HashMap<>();
			attributes.put(DocumentConstants.ID, id);
			destinationSubElement = DocumentUtility.findElementByNameAndValue(destinationElement, name, attributes,
					false);
		} else {
			destinationSubElement = DocumentUtility.findElementByNameAndValue(destinationElement, name, null, false);
		}
		if (destinationSubElement == null) {
			destinationSubElement = destinationElement.createChild(name);
		}

		mergeElements(destinationSubElement, sourceSubElement);
	}

	private static void mergeElements(Element destination, Element source) {
		mergeElementAttributes(destination, source);

		destination.setText(source.getText());

		List<Element> destSubElements = new ArrayList<Element>();
		
		boolean destSubEmpty = destSubElements.isEmpty();

		List<Element> sourceSubElements = source.children();
		for (Element sourceSubElement : sourceSubElements) {
			String name = sourceSubElement.getName();
			String id = sourceSubElement.getAttribute(DocumentConstants.ID);

			Element destinationSubElement = null;
			
			if (!destSubEmpty) {
				if (!Strings.isNullOrEmpty(id)) {
					HashMap<String, String> attributes = new HashMap<>();
					attributes.put(DocumentConstants.ID, id);
					destinationSubElement = DocumentUtility.findElementByNameAndValue(destination, name, attributes, false);
				} else {
					destinationSubElement = DocumentUtility.findElementByNameAndValue(destination, name, null, false);
					// if previously created element found then skip to avoid
					// rewrite
					if (destSubElements.contains(destinationSubElement)) {
						destinationSubElement = null;
					}
				}
			}

			if (destinationSubElement == null) {
				destinationSubElement = destination.createChild(name);
			}

			mergeElements(destinationSubElement, sourceSubElement);
		}
	}

	public static Document createEventDataDocument() {
		Document document = new DocumentImpl();
		document.createRoot(DocumentConstants.EVENT_DATA);

		return document;
	}

}
