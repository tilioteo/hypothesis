/**
 * 
 */
package com.tilioteo.hypothesis.builder.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.tilioteo.common.Strings;
import com.tilioteo.hypothesis.builder.BuilderConstants;
import com.tilioteo.hypothesis.utility.XmlUtility;

/**
 * @author kamil
 *
 */
public class XmlDocumentFactory {

	private static Logger log = Logger.getLogger(XmlDocumentFactory.class);

	@SuppressWarnings("unchecked")
	public static Document mergeSlideDocument(Document templateDocument, Document contentDocument) {
		log.debug(String.format("buildSlideXml(...): template UID = %s", getTemplateUID(templateDocument)));

		Document doc = XmlUtility.createDocument();
		Element root = doc.addElement(BuilderConstants.SLIDE);

		// copy template
		List<Element> templateNodes = templateDocument.getRootElement().elements();
		for (Element node : templateNodes) {
			root.add((Node) node.clone());
		}

		// bind component content
		List<Node> contentNodes = contentDocument.getRootElement()
				.selectNodes(String.format("%s//%s", BuilderConstants.BINDINGS, BuilderConstants.BIND));
		for (Node node : contentNodes) {
			if (node instanceof Element) {
				Element element = (Element) ((Element) node).elements().get(0);
				if (element != null) {
					String name = element.getName();
					String prefix = element.getNamespacePrefix();
					String uri = element.getNamespaceURI();

					String id = XmlDocumentUtility.getId(element);
					if (!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(id)) {
						Element origElement = XmlUtility.findElementByNameAndValue(true, root, name, prefix, uri,
								BuilderConstants.ID, id);
						if (origElement != null) {
							mergeElementAttributes(origElement, element);
							List<Element> bindNodes = element.elements();

							for (Element bindNode : bindNodes) {
								mergeBindingNodes(doc, origElement, bindNode);
							}
						}
					}
				}
			}
		}

		return doc;
	}

	private static String getTemplateUID(Document slideTemplate) {
		return slideTemplate.getRootElement().attributeValue(BuilderConstants.UID);
	}

	private static void mergeBindingNodes(Document doc, Element destinationElement, Element sourceSubElement) {
		// log.debug("mergeBindingNodes(...)");

		String name = sourceSubElement.getName();
		String prefix = sourceSubElement.getNamespacePrefix();
		String uri = sourceSubElement.getNamespaceURI();

		String id = XmlDocumentUtility.getId(sourceSubElement);

		Element destinationSubElement = null;
		if (!Strings.isNullOrEmpty(id)) {
			destinationSubElement = XmlUtility.findElementByNameAndValue(false, destinationElement, name, prefix, uri,
					BuilderConstants.ID, id);
		} else {
			destinationSubElement = XmlUtility.findElementByNameAndValue(false, destinationElement, name, prefix, uri,
					null, null);
		}
		if (destinationSubElement == null) {
			destinationSubElement = destinationElement.addElement(name);
		}

		mergeElements(destinationSubElement, sourceSubElement);
	}

	@SuppressWarnings("unchecked")
	private static void mergeElementAttributes(Element destination, Element source) {
		// log.debug("mergeElementAttributes(...)");

		List<Attribute> sourceAttributes = source.attributes();
		for (Attribute sourceAttribute : sourceAttributes) {
			destination.addAttribute(sourceAttribute.getName(), sourceAttribute.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	private static void mergeElements(Element destination, Element source) {
		mergeElementAttributes(destination, source);

		destination.setText(source.getText());

		List<Node> destSubNodes = new ArrayList<Node>();

		List<Node> sourceNodes = source.selectNodes("*");
		for (Node sourceNode : sourceNodes) {
			if (sourceNode instanceof Element) {
				Element sourceSubElement = (Element) sourceNode;
				String name = sourceSubElement.getName();
				String prefix = sourceSubElement.getNamespacePrefix();
				String uri = sourceSubElement.getNamespaceURI();

				String id = XmlDocumentUtility.getId(sourceSubElement);

				Element destinationSubElement = null;
				if (!Strings.isNullOrEmpty(id)) {
					destinationSubElement = XmlUtility.findElementByNameAndValue(false, destination, name, prefix, uri,
							BuilderConstants.ID, id);
				} else {
					destinationSubElement = XmlUtility.findElementByNameAndValue(false, destination, name, prefix, uri,
							null, null);
					// if previously created element found then skip to avoid
					// rewrite
					if (destSubNodes.contains(destinationSubElement)) {
						destinationSubElement = null;
					}
				}
				if (destinationSubElement == null) {
					destinationSubElement = destination.addElement(name);
					destSubNodes.add(destinationSubElement);
				}
				// destination.add((Node) sourceNode.clone());
				// } else {
				mergeElements(destinationSubElement, sourceSubElement);
			}
		}
	}

}
