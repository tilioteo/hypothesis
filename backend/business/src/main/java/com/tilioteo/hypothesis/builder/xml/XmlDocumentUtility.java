/**
 * 
 */
package com.tilioteo.hypothesis.builder.xml;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.tilioteo.common.Strings;
import com.tilioteo.common.collections.StringMap;
import com.tilioteo.common.collections.StringSet;
import com.tilioteo.hypothesis.builder.BuilderConstants;
import com.tilioteo.hypothesis.utility.XmlUtility;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class XmlDocumentUtility implements Serializable {

	private static Logger log = Logger.getLogger(XmlDocumentUtility.class);

	public static String getTrimmedText(Element element) {
		if (element != null) {
			return element.getText().trim();
		} else {
			return null;
		}
	}

	public static boolean isValidSlideXml(Document doc) {
		return (doc != null && doc.getRootElement() != null
				&& doc.getRootElement().getName().equals(BuilderConstants.SLIDE));
	}

	public static String getAction(Element element) {
		return element.attributeValue(BuilderConstants.ACTION);
	}

	public static Element getActionElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(BuilderConstants.ACTION);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getActionsElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!BuilderConstants.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			List<Element> actions = documentRoot
					.selectNodes(String.format("%s//%s", BuilderConstants.ACTIONS, BuilderConstants.ACTION));

			return actions;
		}

		return null;
	}

	public static String getCaption(Element element) {
		return element.attributeValue(BuilderConstants.CAPTION);
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getCaseElements(Element element) {
		return element.selectNodes(BuilderConstants.CASE);
	}

	public static List<Element> getComponentHandlers(Element component) {
		return getElementSubNodeChilds(component, BuilderConstants.HANDLERS, null);
	}

	public static List<Element> getComponentItems(Element component) {
		return getElementSubNodeChilds(component, BuilderConstants.ITEMS, null);
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getComponentSources(Element component) {
		return component.selectNodes(String.format("%s//%s", BuilderConstants.SOURCES, BuilderConstants.SOURCE));

	}

	public static List<Element> getComponentValidators(Element field) {
		return getElementSubNodeChilds(field, BuilderConstants.VALIDATORS, null);
	}

	public static List<Element> getComponentProperties(Element component) {
		return getElementSubNodeChilds(component, BuilderConstants.PROPERTIES, null);
	}

	public static List<Element> getContainerComponents(Element container, StringSet valids) {
		return getElementSubNodeChilds(container, BuilderConstants.COMPONENTS, valids);
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getElementSubNodeChilds(Element element, String subNodeName,
			StringSet validElementNames) {
		if (element != null && !Strings.isNullOrEmpty(subNodeName)) {
			List<Node> childs = element.selectNodes(String.format("%s/*", subNodeName));

			List<Element> elements = new LinkedList<Element>();
			for (Node child : childs) {
				if (child instanceof Element) {
					Element childElement = (Element) child;
					String childName = childElement.getName();
					if (!Strings.isNullOrEmpty(childElement.getNamespacePrefix())) {
						childName = childElement.getQualifiedName();
					}

					if (validElementNames == null
							|| (validElementNames != null && validElementNames.contains(childName))) {
						elements.add(childElement);
					} else if (validElementNames != null) {
						log.warn(String.format("Xml element '%s' ignored inside of element '%s'", childName,
								element.getName()));
					}
				}
			}

			return elements;
		}

		return null;
	}

	public static Element getExpressionElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(BuilderConstants.EXPRESSION);
		}

		return null;
	}

	public static Element getFalseElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(BuilderConstants.FALSE);
		}

		return null;
	}

	public static Element getMessageElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(BuilderConstants.MESSAGE);
		}

		return null;
	}

	public static Element getMinElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(BuilderConstants.MIN);
		}

		return null;
	}

	public static Element getMaxElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(BuilderConstants.MAX);
		}

		return null;
	}

	public static String getId(Element element) {
		return element.attributeValue(BuilderConstants.ID);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Element> getInputValueElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!BuilderConstants.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			List<Element> elements = (List<Element>) (List) XmlUtility.findNodesByNameStarting(documentRoot,
					BuilderConstants.INPUT_VALUE);
			return elements;
		}

		return null;
	}

	public static List<Element> getItemsElements(Element component, StringSet valids) {
		return getElementSubNodeChilds(component, BuilderConstants.ITEMS, valids);
	}

	public static String getName(Element element) {
		return element.attributeValue(BuilderConstants.NAME);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Element> getOutputValueElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!BuilderConstants.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			List<Element> elements = (List<Element>) (List) XmlUtility.findNodesByNameStarting(documentRoot,
					BuilderConstants.OUTPUT_VALUE);
			return elements;
		}

		return null;
	}

	public static Element getReferenceSubElement(Element element) {
		if (element != null) {
			Element reference = (Element) element.selectSingleNode(BuilderConstants.REFERENCE);
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
			Element instance = (Element) element.selectSingleNode(BuilderConstants.INSTANCE);
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
			return (Element) element.selectSingleNode(BuilderConstants.TRUE);
		}

		return null;
	}

	public static String getType(Element element) {
		return element.attributeValue(BuilderConstants.TYPE);
	}

	public static String getValue(Element element) {
		return element.attributeValue(BuilderConstants.VALUE);
	}

	public static String getValues(Element element) {
		return element.attributeValue(BuilderConstants.VALUES);
	}

	public static String getKey(Element element) {
		return element.attributeValue(BuilderConstants.KEY);
	}

	public static String getUid(Element element) {
		return element.attributeValue(BuilderConstants.UID);
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
			return Strings.toDouble(subElement.attributeValue(BuilderConstants.VALUE));
		}

		return null;
	}

	public static Double getNumberValidatorMaxValue(Element element) {
		Element subElement = getMaxElement(element);
		if (subElement != null) {
			return Strings.toDouble(subElement.attributeValue(BuilderConstants.VALUE));
		}

		return null;
	}

	public static Date getDateValidatorMinValue(Element element, String defaultFormat) {
		Element subElement = getMinElement(element);
		if (subElement != null) {
			String format = subElement.attributeValue(BuilderConstants.FORMAT);
			if (Strings.isNullOrEmpty(format)) {
				format = defaultFormat;
			}
			return Strings.toDate(subElement.attributeValue(BuilderConstants.VALUE), format);
		}

		return null;
	}

	public static Date getDateValidatorMaxValue(Element element, String defaultFormat) {
		Element subElement = getMaxElement(element);
		if (subElement != null) {
			String format = subElement.attributeValue(BuilderConstants.FORMAT);
			if (Strings.isNullOrEmpty(format)) {
				format = defaultFormat;
			}
			return Strings.toDate(subElement.attributeValue(BuilderConstants.VALUE), format);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getVariablesElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!BuilderConstants.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			List<Element> variables = documentRoot
					.selectNodes(String.format("%s//%s", BuilderConstants.VARIABLES, BuilderConstants.VARIABLE));

			return variables;
		}

		return null;
	}

	public static Element getVieportRootElement(Element documentRoot) {
		return getViewportOrWindowRootElement(getViewportElement(documentRoot));
	}

	private static Element getViewportElement(Element documentRoot) {
		if (documentRoot != null) {
			if (!BuilderConstants.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Node node = XmlUtility.findFirstNodeByName(documentRoot, BuilderConstants.VIEWPORT);
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
				if (BuilderConstants.VALID_VIEWPORT_CHILDREN.contains(element.getName())) {
					return element;
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static Element getViewportOrWindowRootElement(Element element) {
		if (element != null) {
			if (element.getName().equals(BuilderConstants.VIEWPORT)
					|| element.getName().equals(BuilderConstants.WINDOW)) {
				return element;
			}
			List<Element> childs = element.elements();
			if (childs.size() > 0) {
				// find first valid child element
				for (Element child : childs) {
					if (BuilderConstants.VALID_VIEWPORT_CHILDREN.contains(child.getName())) {
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
			if (!BuilderConstants.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			List<Element> windows = documentRoot
					.selectNodes(String.format("%s//%s", BuilderConstants.WINDOWS, BuilderConstants.WINDOW));

			return windows;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getTimersElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!BuilderConstants.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			List<Element> timers = documentRoot
					.selectNodes(String.format("%s//%s", BuilderConstants.TIMERS, BuilderConstants.TIMER));

			return timers;
		}

		return null;
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
			Attribute value = element.attribute(BuilderConstants.VALUE);
			if (value != null)
				map.put(name, value.getValue());
		}

		return map;
	}

	public static boolean isValidBranchXml(Document doc) {
		return (doc != null && doc.getRootElement() != null
				&& doc.getRootElement().getName().equals(BuilderConstants.BRANCH));
	}

	public static Element getBranchKeyElement(Element element) {
		if (element != null) {
			Node node = XmlUtility.findFirstNodeByName(element, BuilderConstants.BRANCH_KEY);
			if (node != null && node instanceof Element) {
				return (Element) node;
			}
		}

		return null;
	}

	public static Element getDefaultPathElement(Element documentRoot) {
		if (documentRoot != null) {
			if (!BuilderConstants.BRANCH.equals(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Node node = XmlUtility.findFirstNodeByName(documentRoot, BuilderConstants.DEFAULT_PATH);
			if (node != null && node instanceof Element) {
				return (Element) node;
			}
		}

		return null;
	}

	public static Element getPatternElement(Element element) {
		if (element != null) {
			Node node = XmlUtility.findFirstNodeByName(element, BuilderConstants.PATTERN);
			if (node != null && node instanceof Element) {
				return (Element) node;
			}
		}

		return null;
	}

	/*
	 * public static Element getExpressionElement(Element element) { if (element
	 * != null) { Node node = XmlUtility.findFirstNodeByName(element,
	 * BranchXmlConstants.EXPRESSION); if (node != null && node instanceof
	 * Element) { return (Element) node; } }
	 * 
	 * return null; }
	 */

	public static Long getSlideId(Element element) {
		if (element != null) {
			String idString = element.attributeValue(BuilderConstants.SLIDE_ID);
			if (!Strings.isNullOrEmpty(idString)) {
				try {
					Long id = Long.parseLong(idString);
					return id;
				} catch (NumberFormatException e) {
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getNickElements(Element patternElement) {
		if (patternElement != null) {
			List<Element> nicks = patternElement.selectNodes(BuilderConstants.NICK);
			return nicks;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getPathElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!BuilderConstants.BRANCH.equals(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			List<Element> paths = documentRoot.selectNodes(BuilderConstants.PATH);
			return paths;
		}

		return null;
	}

	public static boolean isValidTaskXml(Document document) {
		return (document != null && document.getRootElement() != null
				&& document.getRootElement().getName().equals(BuilderConstants.TASK));
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getNodesElements(Element element) {
		if (element != null) {
			List<Element> variables = element
					.selectNodes(String.format("%s//%s", BuilderConstants.NODES, BuilderConstants.NODE));

			return variables;
		}

		return null;
	}

	public static Element getEvaluateElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(BuilderConstants.EVALUATE);
		}

		return null;
	}

}
