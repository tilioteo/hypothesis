/**
 * 
 */
package com.tilioteo.hypothesis.dom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.tilioteo.common.Strings;
import com.tilioteo.hypothesis.entity.Slide;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class SlideXmlFactory implements Serializable {

	private static Logger log = Logger.getLogger(SlideXmlFactory.class);

	@SuppressWarnings("unchecked")
	private static Document buildSlideXml(Document slideTemplate, Document slideContent) {
		log.debug(String.format("buildSlideXml(...): template UID = %s", getTemplateUID(slideTemplate)));
		
		
		
		Document doc = XmlUtility.createDocument();
		Element root = doc.addElement(SlideXmlConstants.SLIDE);

		// copy template
		List<Element> templateNodes = slideTemplate.getRootElement().elements();
		for (Element node : templateNodes) {
			root.add((Node) node.clone());
		}

		// bind component content
		List<Node> contentNodes = slideContent.getRootElement().selectNodes(
				String.format("%s//%s", SlideXmlConstants.BINDINGS,
						SlideXmlConstants.BIND));
		for (Node node : contentNodes) {
			if (node instanceof Element) {
				Element element = (Element) ((Element) node).elements().get(0);
				if (element != null) {
					String name = element.getName();
					String prefix = element.getNamespacePrefix();
					String uri = element.getNamespaceURI();
					
					String id = SlideXmlUtility.getId(element);
					if (!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(id)) {
						Element origElement = XmlUtility.findElementByNameAndValue(true, root, name, prefix, uri, SlideXmlConstants.ID, id);
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
	
	/**
	 * for logging purpose only
	 * @return
	 */
	private static String getTemplateUID(Document slideTemplate) {
		return slideTemplate.getRootElement().attributeValue(SlideXmlConstants.UID);
	}

	public static Document buildSlideXml(Slide slide) {
		if (null == slide) {
			log.warn("getSlideXml(null)");
			return null;
		}
		
		log.debug(String.format("getSlideXml(slide:%d)", slide.getId()));

		Document slideTemplate = XmlUtility.readString(slide.getTemplateXmlData());
		Document slideContent = XmlUtility.readString(slide.getXmlData());

		if (slideTemplate == null)
			log.warn(String.format("Template document is NULL (slide:%d)", slide.getId()));
		if (slideContent == null)
			log.warn(String.format("Content document is NULL (slide:%d)", slide.getId()));

		try {
			if (slideTemplate != null && slideContent != null) {
				return buildSlideXml(slideTemplate, slideContent);
			} else {
				return null;
			}
		} catch (Throwable t) {
			log.error(String.format("getSlideXml(slide:%d)",
					slide != null ? slide.getId() : -1), t);
			return null;
		}
	}

	public static Document createEventDataXml() {
		Document eventData = XmlUtility.createDocument();
		eventData.addElement(SlideXmlConstants.EVENT_DATA);
		return eventData;
	}

	private static void mergeBindingNodes(Document doc,	Element destinationElement, Element sourceSubElement) {
		//log.debug("mergeBindingNodes(...)");
		
		String name = sourceSubElement.getName();
		String prefix = sourceSubElement.getNamespacePrefix();
		String uri = sourceSubElement.getNamespaceURI();
		
		String id = SlideXmlUtility.getId(sourceSubElement);
		
		Element destinationSubElement = null;
		if (!Strings.isNullOrEmpty(id)) {
			destinationSubElement = XmlUtility.findElementByNameAndValue(false, destinationElement, name, prefix, uri, SlideXmlConstants.ID, id);
		} else {
			destinationSubElement = XmlUtility.findElementByNameAndValue(false, destinationElement, name, prefix, uri, null, null);
		}
		if (destinationSubElement == null) {
			destinationSubElement = destinationElement.addElement(name);
		}

		mergeElements(destinationSubElement, sourceSubElement);
	}

	@SuppressWarnings("unchecked")
	private static void mergeElementAttributes(Element destination,
			Element source) {
		//log.debug("mergeElementAttributes(...)");
		
		List<Attribute> sourceAttributes = source.attributes();
		for (Attribute sourceAttribute : sourceAttributes) {
			destination.addAttribute(sourceAttribute.getName(),
					sourceAttribute.getValue());
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
				Element sourceSubElement = (Element)sourceNode;
				String name = sourceSubElement.getName();
				String prefix = sourceSubElement.getNamespacePrefix();
				String uri = sourceSubElement.getNamespaceURI();
				
				String id = SlideXmlUtility.getId(sourceSubElement);
				
				Element destinationSubElement = null;
				if (!Strings.isNullOrEmpty(id)) {
					destinationSubElement = XmlUtility.findElementByNameAndValue(false, destination, name, prefix, uri, SlideXmlConstants.ID, id);
				} else {
					destinationSubElement = XmlUtility.findElementByNameAndValue(false, destination, name, prefix, uri, null, null);
					// if previously created element found then skip to avoid rewrite
					if (destSubNodes.contains(destinationSubElement)) {
						destinationSubElement = null;
					}
				}
				if (destinationSubElement == null) {
					destinationSubElement = destination.addElement(name);
					destSubNodes.add(destinationSubElement);
				}
					//destination.add((Node) sourceNode.clone());
				//} else {
					mergeElements(destinationSubElement, sourceSubElement);
			}
		}
	}

}
