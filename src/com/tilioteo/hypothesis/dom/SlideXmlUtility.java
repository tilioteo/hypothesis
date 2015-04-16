/**
 * 
 */
package com.tilioteo.hypothesis.dom;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.StringSet;
import com.tilioteo.hypothesis.common.Strings;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class SlideXmlUtility {

	private static Logger log = Logger.getLogger(SlideXmlUtility.class);

	public static String getAction(Element element) {
		return element.attributeValue(SlideXmlConstants.ACTION);
	}

	public static Element getActionElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(SlideXmlConstants.ACTION);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getActionsElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!SlideXmlConstants.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			List<Element> actions = documentRoot.selectNodes(String.format(
					"%s//%s", SlideXmlConstants.ACTIONS,
					SlideXmlConstants.ACTION));

			return actions;
		}

		return null;
	}

	public static String getCaption(Element element) {
		return element.attributeValue(SlideXmlConstants.CAPTION);
	}

	public static List<Element> getCaseElements(Element element) {
		return getElementSubNodeChilds(element, SlideXmlConstants.CASE, null);
	}

	public static List<Element> getComponentHandlers(Element component) {
		return getElementSubNodeChilds(component, SlideXmlConstants.HANDLERS,
				null);
	}

	public static List<Element> getComponentItems(Element component) {
		return getElementSubNodeChilds(component, SlideXmlConstants.ITEMS, null);
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getComponentSources(Element component) {
		return component.selectNodes(String.format(
				"%s//%s", SlideXmlConstants.SOURCES,
				SlideXmlConstants.SOURCE));

	}

	public static List<Element> getComponentValidators(Element field) {
		return getElementSubNodeChilds(field, SlideXmlConstants.VALIDATORS,
				null);
	}

	public static List<Element> getComponentProperties(Element component) {
		return getElementSubNodeChilds(component, SlideXmlConstants.PROPERTIES,
				null);
	}

	public static List<Element> getContainerComponents(Element container,
			StringSet valids) {
		return getElementSubNodeChilds(container, SlideXmlConstants.COMPONENTS,
				valids);
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getElementSubNodeChilds(Element element, String subNodeName, StringSet validElementNames) {
		if (element != null && !Strings.isNullOrEmpty(subNodeName)) {
			List<Node> childs = element.selectNodes(String.format("%s/*", subNodeName));

			List<Element> elements = new LinkedList<Element>();
			for (Node child : childs) {
				if (child instanceof Element) {
					Element childElement = (Element)child;
					String childName = childElement.getName();
					if (!Strings.isNullOrEmpty(childElement.getNamespacePrefix())) {
						childName = childElement.getQualifiedName();
					}
					
					if (validElementNames == null || (validElementNames != null && validElementNames.contains(childName))) {
						elements.add(childElement);
					} else if (validElementNames != null) {
						log.warn(String.format("Xml element '%s' ignored inside of element '%s'",
										childName, element.getName()));
					}
				}
			}

			return elements;
		}

		return null;
	}

	public static Element getExpressionElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(SlideXmlConstants.EXPRESSION);
		}

		return null;
	}

	public static Element getFalseElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(SlideXmlConstants.FALSE);
		}

		return null;
	}

	public static Element getMessageElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(SlideXmlConstants.MESSAGE);
		}

		return null;
	}

	public static Element getMinElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(SlideXmlConstants.MIN);
		}

		return null;
	}

	public static Element getMaxElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(SlideXmlConstants.MAX);
		}

		return null;
	}

	public static String getId(Element element) {
		return element.attributeValue(SlideXmlConstants.ID);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Element> getInputValueElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!SlideXmlConstants.VALID_SLIDE_ROOT_ELEMENTS
					.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			List<Element> elements = (List<Element>)(List)XmlUtility.findNodesByNameStarting(documentRoot, SlideXmlConstants.INPUT_VALUE);
			return  elements;
		}

		return null;
	}

	public static List<Element> getItemsElements(Element component,
			StringSet valids) {
		return getElementSubNodeChilds(component, SlideXmlConstants.ITEMS,
				valids);
	}

	public static String getName(Element element) {
		return element.attributeValue(SlideXmlConstants.NAME);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Element> getOutputValueElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!SlideXmlConstants.VALID_SLIDE_ROOT_ELEMENTS
					.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			List<Element> elements = (List<Element>)(List)XmlUtility.findNodesByNameStarting(documentRoot, SlideXmlConstants.OUTPUT_VALUE);
			return  elements;
		}

		return null;
	}

	public static Element getReferenceSubElement(Element element) {
		if (element != null) {
			Element reference = (Element) element
					.selectSingleNode(SlideXmlConstants.REFERENCE);
			if (reference != null) {
				@SuppressWarnings("unchecked")
				List<Element> elements = reference.elements();
				if (elements.size() > 0)
					return elements.get(0);
			}
		}

		return null;
	}

	public static Element getInstanceSubElement(Element element) {
		if (element != null) {
			Element instance = (Element) element
					.selectSingleNode(SlideXmlConstants.INSTANCE);
			if (instance != null) {
				@SuppressWarnings("unchecked")
				List<Element> elements = instance.elements();
				if (elements.size() > 0)
					return elements.get(0);
			}
		}

		return null;
	}

	public static Element getTrueElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(SlideXmlConstants.TRUE);
		}

		return null;
	}

	public static String getType(Element element) {
		return element.attributeValue(SlideXmlConstants.TYPE);
	}

	public static String getValue(Element element) {
		return element.attributeValue(SlideXmlConstants.VALUE);
	}

	public static String getValues(Element element) {
		return element.attributeValue(SlideXmlConstants.VALUES);
	}

	public static String getKey(Element element) {
		return element.attributeValue(SlideXmlConstants.KEY);
	}

	public static String getValidatorMessage(Element element, String defaultMessage) {
		Element messageElement = getMessageElement(element);
		if (messageElement != null) {
			String message = messageElement.getTextTrim();
			if (Strings.isNullOrEmpty(message)) {
				return defaultMessage;
			}
			return message;
		}
		
		return defaultMessage;
	}
	
	public static Double getNumberValidatorMinValue(Element element) {
		Element subElement = getMinElement(element);
		if (subElement != null) {
			return Strings.toDouble(subElement.attributeValue(SlideXmlConstants.VALUE));
		}
		
		return null;
	}

	public static Double getNumberValidatorMaxValue(Element element) {
		Element subElement = getMaxElement(element);
		if (subElement != null) {
			return Strings.toDouble(subElement.attributeValue(SlideXmlConstants.VALUE));
		}
		
		return null;
	}

	public static Date getDateValidatorMinValue(Element element, String defaultFormat) {
		Element subElement = getMinElement(element);
		if (subElement != null) {
			String format = subElement.attributeValue(SlideXmlConstants.FORMAT);
			if (Strings.isNullOrEmpty(format)) {
				format = defaultFormat;
			}
			return Strings.toDate(subElement.attributeValue(SlideXmlConstants.VALUE), format);
		}
		
		return null;
	}

	public static Date getDateValidatorMaxValue(Element element, String defaultFormat) {
		Element subElement = getMaxElement(element);
		if (subElement != null) {
			String format = subElement.attributeValue(SlideXmlConstants.FORMAT);
			if (Strings.isNullOrEmpty(format)) {
				format = defaultFormat;
			}
			return Strings.toDate(subElement.attributeValue(SlideXmlConstants.VALUE), format);
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getVariablesElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!SlideXmlConstants.VALID_SLIDE_ROOT_ELEMENTS
					.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			List<Element> variables = documentRoot.selectNodes(String.format(
					"%s//%s", SlideXmlConstants.VARIABLES,
					SlideXmlConstants.VARIABLE));

			return variables;
		}

		return null;
	}

	public static Element getVieportRootElement(Element documentRoot) {
		return getViewportOrWindowRootElement(getViewportElement(documentRoot));
	}

	private static Element getViewportElement(Element documentRoot) {
		if (documentRoot != null) {
			if (!SlideXmlConstants.VALID_SLIDE_ROOT_ELEMENTS
					.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Node node = XmlUtility.findFirstNodeByName(documentRoot,
					SlideXmlConstants.VIEWPORT);
			if (node != null && node instanceof Element) {
				return (Element) node;
			} else {
				// throw new DocumentRootNoViewportException(documentRoot);
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static Element getViewportInnerComponent(Element documentRoot) {
		Element viewportRootElement = getVieportRootElement(documentRoot);
		if (viewportRootElement != null) {
			List<Element> elements = viewportRootElement.elements();
			for (Element element : elements) {
				if (SlideXmlConstants.VALID_VIEWPORT_ELEMENTS.contains(element
						.getName())) {
					return element;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Element getViewportOrWindowRootElement(Element element) {
		if (element != null) {
			if (element.getName().equals(SlideXmlConstants.VIEWPORT)
					|| element.getName().equals(SlideXmlConstants.WINDOW)) {
				return element;
			}
			List<Element> childs = element.elements();
			if (childs.size() > 0) {
				// find first valid child element
				for (Element child : childs) {
					if (SlideXmlConstants.VALID_VIEWPORT_ELEMENTS
							.contains(child.getName())) {
						return child;
					}
				}
				// throw new ViewportNotValidElementFound(viewport);
			} else {
				// throw new ViewportNoElementsException(viewport);
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getWindowsElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!SlideXmlConstants.VALID_SLIDE_ROOT_ELEMENTS
					.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			List<Element> windows = documentRoot.selectNodes(String.format(
					"%s//%s", SlideXmlConstants.WINDOWS,
					SlideXmlConstants.WINDOW));

			return windows;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getTimersElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!SlideXmlConstants.VALID_SLIDE_ROOT_ELEMENTS
					.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			List<Element> timers = documentRoot.selectNodes(String.format(
					"%s//%s", SlideXmlConstants.TIMERS,
					SlideXmlConstants.TIMER));

			return timers;
		}

		return null;
	}

	public static boolean isValidSlideXml(Document doc) {
		return (doc != null && doc.getRootElement() != null && doc
				.getRootElement().getName().equals(SlideXmlConstants.SLIDE));
	}
	
	@SuppressWarnings("unchecked")
	public static StringMap getActionAttributesMap(Element element) {
		StringMap map = new StringMap();

		List<Attribute> attributes = element.attributes();
		for (Attribute attribute : attributes) {
			map.put(attribute.getName(), attribute.getValue());
		}

		return map;
	}

	public static StringMap getPropertyValueMap(Element component) {
		StringMap map = new StringMap();
		List<Element> elements = getComponentProperties(component);

		for (Element element : elements) {
			String name = element.getName();
			Attribute value = element.attribute(SlideXmlConstants.VALUE);
			if (value != null)
				map.put(name, value.getValue());
		}

		return map;
	}
	

}
