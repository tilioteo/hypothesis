/**
 * 
 */
package org.hypothesis.application.collector.xml;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.hypothesis.common.StringSet;
import org.hypothesis.common.Strings;
import org.hypothesis.common.xml.Utility;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class SlideXmlUtility {

	private static Log log = LogFactory.getLog(SlideXmlUtility.class);

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
			if (!SlideXmlConstants.VALID_SLIDE_ROOT_ELEMENTS
					.contains(documentRoot.getName())) {
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
	private static List<Element> getElementSubNodeChilds(Element element,
			String subNodeName, StringSet validElementNames) {
		if (element != null && !Strings.isNullOrEmpty(subNodeName)) {
			List<Node> childs = element.selectNodes(String.format("%s/*",
					subNodeName));

			List<Element> elements = new LinkedList<Element>();
			for (Node child : childs) {
				if (child instanceof Element) {
					if (validElementNames == null || (validElementNames != null && validElementNames
							.contains(child.getName()))) {
						elements.add((Element) child);
					} else if (validElementNames != null) {
						log.warn(String.format("Xml element '%s' ignored inside of element '%s'", child.getName(), element.getName()));
					}
				}
			}

			return elements;
		}

		return null;
	}

	public static Element getExpressionElement(Element element) {
		if (element != null) {
			return (Element) element
					.selectSingleNode(SlideXmlConstants.EXPRESSION);
		}

		return null;
	}

	public static Element getFalseElement(Element element) {
		if (element != null) {
			return (Element) element.selectSingleNode(SlideXmlConstants.FALSE);
		}

		return null;
	}

	public static String getId(Element element) {
		return element.attributeValue(SlideXmlConstants.ID);
	}

	public static Element getInputValueElement(Element documentRoot) {
		if (documentRoot != null) {
			if (!SlideXmlConstants.VALID_SLIDE_ROOT_ELEMENTS
					.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Node node = Utility.findFirstNodeByName(documentRoot,
					SlideXmlConstants.INPUT_VALUE);
			if (node != null && node instanceof Element) {
				return (Element) node;
			} else {
				// throw new DocumentRootNoViewportException(documentRoot);
			}
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

	public static Element getOutputValueElement(Element documentRoot) {
		if (documentRoot != null) {
			if (!SlideXmlConstants.VALID_SLIDE_ROOT_ELEMENTS
					.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Node node = Utility.findFirstNodeByName(documentRoot,
					SlideXmlConstants.OUTPUT_VALUE);
			if (node != null && node instanceof Element) {
				return (Element) node;
			} else {
				// throw new DocumentRootNoViewportException(documentRoot);
			}
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

			Node node = Utility.findFirstNodeByName(documentRoot,
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

	public static boolean isValidSlideXml(Document doc) {
		return (doc != null && doc.getRootElement() != null && doc
				.getRootElement().getName().equals(SlideXmlConstants.SLIDE));
	}

	/*
	 * public static String getEvaluationXmlString(Document doc) { if (doc !=
	 * null && isValidSlideXml(doc)) { Node node =
	 * Utility.findFirstNodeByName(doc.getRootElement(),
	 * SlideXmlConstants.EVALUATION); if (node != null) { Document newDoc =
	 * Utility.createDocument(); Element newRoot =
	 * newDoc.addElement(SlideXmlConstants.SLIDE);
	 * newRoot.add((Node)node.clone()); return Utility.writeString(newDoc); } }
	 * 
	 * return null; }
	 */

	/*
	 * public static List<Node> getChoiceNodes(Node parent) { return
	 * getChoiceNodes(parent, true); }
	 * 
	 * public static List<Node> getChoiceNodes(Node parent, boolean sublevel) {
	 * return getNodesByFilter(parent,
	 * SlideXmlNodeFilterConstants.FILTER_CHOICES, sublevel); }
	 */

	/*
	 * public static List<Node> getLayerNodes(Node parent) { return
	 * getLayerNodes(parent, true); }
	 * 
	 * public static List<Node> getLayerNodes(Node parent, boolean sublevel) {
	 * return getNodesByFilter(parent,
	 * SlideXmlNodeFilterConstants.FILTER_LAYERS, sublevel); }
	 * 
	 * public static List<Node> getControlNodes(Node parent) { return
	 * getControlNodes(parent, true); }
	 * 
	 * public static List<Node> getControlNodes(Node parent, boolean sublevel) {
	 * return getNodesByFilter(parent,
	 * SlideXmlNodeFilterConstants.FILTER_CONTROLS, sublevel); }
	 * 
	 * public static List<Node> getStyleNodes(Node parent) { return
	 * getStyleNodes(parent, true); }
	 * 
	 * public static List<Node> getStyleNodes(Node parent, boolean sublevel) {
	 * return getNodesByFilter(parent,
	 * SlideXmlNodeFilterConstants.FILTER_STYLES, sublevel); }
	 * 
	 * public static List<Node> getStyleMapNodes(Node parent) { return
	 * getStyleMapNodes(parent, true); }
	 * 
	 * public static List<Node> getStyleMapNodes(Node parent, boolean sublevel)
	 * { return getNodesByFilter(parent,
	 * SlideXmlNodeFilterConstants.FILTER_STYLE_MAPS, sublevel); }
	 * 
	 * public static List<Node> getIconNodes(Node parent) { return
	 * getIconNodes(parent, true); }
	 * 
	 * public static List<Node> getIconNodes(Node parent, boolean sublevel) {
	 * return getNodesByFilter(parent, SlideXmlNodeFilterConstants.FILTER_ICONS,
	 * sublevel); }
	 * 
	 * public static List<Node> getMarkerNodes(Node parent) { return
	 * getMarkerNodes(parent, true); }
	 * 
	 * public static List<Node> getMarkerNodes(Node parent, boolean sublevel) {
	 * return getNodesByFilter(parent,
	 * SlideXmlNodeFilterConstants.FILTER_MARKERS, sublevel); }
	 * 
	 * public static List<Node> getFeatureNodes(Node parent) { return
	 * getFeatureNodes(parent, true); }
	 * 
	 * public static List<Node> getFeatureNodes(Node parent, boolean sublevel) {
	 * return getNodesByFilter(parent,
	 * SlideXmlNodeFilterConstants.FILTER_FEATURES, sublevel); }
	 * 
	 * public static List<Node> getAttributeNodes(Node parent) { return
	 * getAttributeNodes(parent, true); }
	 * 
	 * public static List<Node> getAttributeNodes(Node parent, boolean sublevel)
	 * { return getNodesByFilter(parent,
	 * SlideXmlNodeFilterConstants.FILTER_ATTRIBUTES, sublevel); }
	 */

}
