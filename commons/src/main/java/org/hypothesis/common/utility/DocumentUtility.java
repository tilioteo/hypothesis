/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.common.utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hypothesis.common.ValidationSets;
import org.hypothesis.interfaces.Action;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;
import org.hypothesis.interfaces.HandlerCallback;
import org.hypothesis.interfaces.SlidePresenter;

import com.tilioteo.common.Strings;
import com.tilioteo.common.collections.StringMap;
import com.tilioteo.common.collections.StringSet;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public final class DocumentUtility {

	private DocumentUtility() {
	}

	/**
	 * Find element by its name and attribute values
	 * 
	 * @param element
	 * @param name
	 * @param attributes
	 * @param descendant
	 * @return
	 */
	public static final Element findElementByNameAndValue(Element element, String name, Map<String, String> attributes,
			boolean descendant) {
		if (element != null) {
			List<Element> elements = element.children();

			for (Element selected : elements) {
				if (selected.getName().equals(name)) {
					if (attributes != null && !attributes.isEmpty()) {
						boolean passed = true;
						Map<String, String> selectedAttributes = selected.attributes();
						for (Entry<String, String> entry : attributes.entrySet()) {
							String key = entry.getKey();
							String value = entry.getValue();
							String selectedValue = selectedAttributes.get(key);
							if (!selectedAttributes.containsKey(key) || (selectedValue == null && value != null)
									|| (selectedValue != null && !selectedValue.equals(value))) {
								passed = false;
								break;
							}
						}
						if (passed) {
							return selected;
						}
					} else {
						return selected;
					}
				} else if (descendant) {
					Element found = findElementByNameAndValue(selected, name, attributes, descendant);
					if (found != null) {
						return found;
					}
				}
			}
		}

		return null;
	}

	public static List<Element> findElementsByNameStarting(Element parent, String startName) {
		if (parent != null && startName.length() > 0) {
			List<Element> result = new ArrayList<>();
			List<Element> elements = parent.children();

			for (Element element : elements) {
				if (element.getName().startsWith(startName)) {
					result.add(element);
				}
			}
			return result;
		}

		return null;
	}

	public static String getTrimmedText(Element element) {
		if (element != null && element.getText() != null) {
			return element.getText().trim();
		}

		return null;
	}

	public static List<Element> getSubElementChildren(Element element, String subNodeName,
			StringSet validElementNames) {
		if (element != null && !Strings.isNullOrEmpty(subNodeName)) {
			Element subElement = element.selectElement(subNodeName);

			if (subElement != null) {
				List<Element> result = new ArrayList<>();
				List<Element> elements = subElement.children();

				for (Element child : elements) {
					String childName = child.getName();

					if (null == validElementNames || validElementNames.contains(childName)) {
						result.add(child);
					}
				}

				return result;
			}
		}

		return null;
	}

	public static final boolean isValidSlideDocument(Document doc) {
		return doc != null && doc.root() != null && doc.root().getName().equals(DocumentConstants.SLIDE);
	}

	public static String getId(Element element) {
		return element.getAttribute(DocumentConstants.ID);
	}

	public static String getCaption(Element element) {
		return element.getAttribute(DocumentConstants.CAPTION);
	}

	public static List<Element> getActionsElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Element element = documentRoot.selectElement(DocumentConstants.ACTIONS);
			if (element != null) {
				return element.selectElements(DocumentConstants.ACTION);
			}
		}

		return null;
	}

	public static Element getExpressionElement(Element element) {
		if (element != null) {
			return element.selectElement(DocumentConstants.EXPRESSION);
		}

		return null;
	}

	public static Element getTrueElement(Element element) {
		if (element != null) {
			return element.selectElement(DocumentConstants.TRUE);
		}

		return null;
	}

	public static Element getFalseElement(Element element) {
		if (element != null) {
			return element.selectElement(DocumentConstants.FALSE);
		}

		return null;
	}

	public static Element getLoopElement(Element element) {
		if (element != null) {
			return element.selectElement(DocumentConstants.LOOP);
		}

		return null;
	}

	public static List<Element> getCaseElements(Element element) {
		return element.selectElements(DocumentConstants.CASE);
	}

	public static String getValue(Element element) {
		return element.getAttribute(DocumentConstants.VALUE);
	}

	public static String getAction(Element element) {
		return element.getAttribute(DocumentConstants.ACTION);
	}

	public static String getType(Element element) {
		return element.getAttribute(DocumentConstants.TYPE);
	}

	public static String getKey(Element element) {
		return element.getAttribute(DocumentConstants.KEY);
	}

	public static String getValues(Element element) {
		return element.getAttribute(DocumentConstants.VALUES);
	}

	public static String getName(Element element) {
		return element.getAttribute(DocumentConstants.NAME);
	}

	public static String getUid(Element element) {
		return element.getAttribute(DocumentConstants.UID);
	}

	public static List<Element> getVariablesElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Element element = documentRoot.selectElement(DocumentConstants.VARIABLES);
			if (element != null) {
				return element.selectElements(DocumentConstants.VARIABLE);
			}
		}

		return null;
	}

	public static Element getReferenceSubElement(Element element) {
		if (element != null) {
			Element reference = element.selectElement(DocumentConstants.REFERENCE);
			if (reference != null) {
				return reference.firstChild();
			}
		}

		return null;
	}

	public static Element getInstanceSubElement(Element element) {
		if (element != null) {
			Element instance = element.selectElement(DocumentConstants.INSTANCE);
			if (instance != null) {
				return instance.firstChild();
			}
		}

		return null;
	}

	public static List<Element> getInputValueElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			return findElementsByNameStarting(documentRoot, DocumentConstants.INPUT_VALUE);
		}

		return null;
	}

	public static List<Element> getOutputValueElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			return findElementsByNameStarting(documentRoot, DocumentConstants.OUTPUT_VALUE);
		}

		return null;
	}

	public static List<Element> getTimersElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Element element = documentRoot.selectElement(DocumentConstants.TIMERS);
			if (element != null) {
				return element.selectElements(DocumentConstants.TIMER);
			}
		}

		return null;
	}

	public static StringMap getPropertyValueMap(Element component) {
		StringMap map = new StringMap();
		List<Element> elements = getComponentProperties(component);

		if (elements != null) {
			for (Element element : elements) {
				map.put(element.getName(), element.getAttribute(DocumentConstants.VALUE));
			}
		}

		return map;
	}

	public static List<Element> getComponentValidators(Element field) {
		return getSubElementChildren(field, DocumentConstants.VALIDATORS, null);
	}

	public static List<Element> getComponentProperties(Element component) {
		return getSubElementChildren(component, DocumentConstants.PROPERTIES, null);
	}

	public static List<Element> getContainerComponents(Element container, StringSet valids) {
		return getSubElementChildren(container, DocumentConstants.COMPONENTS, valids);
	}

	public static List<Element> getComponentHandlers(Element component) {
		return getSubElementChildren(component, DocumentConstants.HANDLERS, null);
	}

	public static List<Element> getComponentItems(Element component) {
		return getSubElementChildren(component, DocumentConstants.ITEMS, null);
	}

	public static List<Element> getComponentSources(Element component) {
		if (component != null) {
			Element element = component.selectElement(DocumentConstants.SOURCES);

			if (element != null) {
				return element.selectElements(DocumentConstants.SOURCE);
			}
		}

		return null;
	}

	public static List<Element> getWindowsElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Element element = documentRoot.selectElement(DocumentConstants.WINDOWS);
			if (element != null) {
				return element.selectElements(DocumentConstants.WINDOW);
			}
		}

		return null;
	}

	public static Element getViewportOrWindowRootElement(Element element) {
		if (element != null) {
			if (element.getName().equals(DocumentConstants.VIEWPORT)
					|| element.getName().equals(DocumentConstants.WINDOW)) {
				return element;
			}

			List<Element> elements = element.children();
			if (!elements.isEmpty()) {
				// find first valid child element
				for (Element child : elements) {
					if (ValidationSets.VALID_VIEWPORT_CHILDREN.contains(child.getName())) {
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

	public static Element getViewportInnerComponent(Element documentRoot) {
		Element viewportRootElement = getVieportRootElement(documentRoot);

		if (viewportRootElement != null) {
			List<Element> elements = viewportRootElement.children();
			for (Element element : elements) {
				if (ValidationSets.VALID_VIEWPORT_CHILDREN.contains(element.getName())) {
					return element;
				}
			}
		}

		return null;
	}

	public static Element getVieportRootElement(Element documentRoot) {
		return getViewportOrWindowRootElement(getViewportElement(documentRoot));
	}

	private static Element getViewportElement(Element documentRoot) {
		if (documentRoot != null) {
			if (!ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Element element = findElementByNameAndValue(documentRoot, DocumentConstants.VIEWPORT, null, true);
			if (element != null) {
				return element;
				// } else {
				// throw new DocumentRootNoViewportException(documentRoot);
			}
		}

		return null;
	}

	public static String getValidatorMessage(Element element, String defaultMessage) {
		Element messageElement = getMessageElement(element);

		if (messageElement != null) {
			String message = getTrimmedText(messageElement);

			if (Strings.isNullOrEmpty(message)) {
				return defaultMessage;
			}

			return message;
		}

		return defaultMessage;
	}

	public static Element getMessageElement(Element element) {
		if (element != null) {
			return element.selectElement(DocumentConstants.MESSAGE);
		}

		return null;
	}

	public static Double getNumberValidatorMinValue(Element element) {
		Element subElement = getMinElement(element);

		if (subElement != null) {
			return Strings.toDouble(subElement.getAttribute(DocumentConstants.VALUE));
		}

		return null;
	}

	public static Double getNumberValidatorMaxValue(Element element) {
		Element subElement = getMaxElement(element);

		if (subElement != null) {
			return Strings.toDouble(subElement.getAttribute(DocumentConstants.VALUE));
		}

		return null;
	}

	public static Element getMinElement(Element element) {
		if (element != null) {
			return element.selectElement(DocumentConstants.MIN);
		}

		return null;
	}

	public static Element getMaxElement(Element element) {
		if (element != null) {
			return element.selectElement(DocumentConstants.MAX);
		}

		return null;
	}

	public static Date getDateValidatorMinValue(Element element, String defaultFormat) {
		Element subElement = getMinElement(element);
		if (subElement != null) {
			String format = subElement.getAttribute(DocumentConstants.FORMAT);

			if (Strings.isNullOrEmpty(format)) {
				format = defaultFormat;
			}

			return Strings.toDate(subElement.getAttribute(DocumentConstants.VALUE), format);
		}

		return null;
	}

	public static Date getDateValidatorMaxValue(Element element, String defaultFormat) {
		Element subElement = getMaxElement(element);
		if (subElement != null) {
			String format = subElement.getAttribute(DocumentConstants.FORMAT);

			if (Strings.isNullOrEmpty(format)) {
				format = defaultFormat;
			}

			return Strings.toDate(subElement.getAttribute(DocumentConstants.VALUE), format);
		}

		return null;
	}

	public static boolean isValidBranchDocument(Document document) {
		return (document != null && document.root() != null
				&& document.root().getName().equals(DocumentConstants.BRANCH));
	}

	public static List<Element> getPathElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!DocumentConstants.BRANCH.equals(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			return documentRoot.selectElements(DocumentConstants.PATH);
		}

		return null;
	}

	public static Element getDefaultPathElement(Element documentRoot) {
		if (documentRoot != null) {
			if (!DocumentConstants.BRANCH.equals(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			return DocumentUtility.findElementByNameAndValue(documentRoot, DocumentConstants.DEFAULT_PATH, null, true);
		}

		return null;
	}

	public static Element getBranchKeyElement(Element element) {
		if (element != null) {
			return DocumentUtility.findElementByNameAndValue(element, DocumentConstants.BRANCH_KEY, null, true);
		}

		return null;
	}

	public static Element getPatternElement(Element element) {
		if (element != null) {
			return DocumentUtility.findElementByNameAndValue(element, DocumentConstants.PATTERN, null, true);
		}

		return null;
	}

	public static List<Element> getNickElements(Element patternElement) {
		if (patternElement != null) {
			return patternElement.selectElements(DocumentConstants.NICK);
		}

		return null;
	}

	public static Long getSlideId(Element element) {
		if (element != null) {
			String idString = element.getAttribute(DocumentConstants.SLIDE_ID);
			if (!Strings.isNullOrEmpty(idString)) {
				try {
					return Long.parseLong(idString);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	public static boolean isValidTaskDocument(Document document) {
		return (document != null && document.root() != null
				&& document.root().getName().equals(DocumentConstants.TASK));
	}

	public static List<Element> getNodesElements(Element element) {
		if (element != null) {
			Element variable = element.selectElement(DocumentConstants.NODES);

			if (variable != null) {
				return variable.selectElements(DocumentConstants.NODE);
			}
		}

		return null;
	}

	public static Element getEvaluateElement(Element element) {
		if (element != null) {
			return element.selectElement(DocumentConstants.EVALUATE);
		}

		return null;
	}

	public static final boolean isValidMessageDocument(Document document) {
		return document != null && document.root() != null
				&& document.root().getName().equals(DocumentConstants.MESSAGE);
	}

	public static List<Element> getPropertyElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!DocumentConstants.MESSAGE.equals(documentRoot.getName())) {
				return null;
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Element element = documentRoot.selectElement(DocumentConstants.PROPERTIES);
			if (element != null) {
				return element.selectElements(DocumentConstants.PROPERTY);
			}
		}

		return null;
	}

	public static void iterateHandlers(Component component, Element element, SlidePresenter presenter,
			HandlerCallback callback) {
		List<Element> handlers = DocumentUtility.getComponentHandlers(element);

		if (handlers != null) {
			for (Element handler : handlers) {
				String name = handler.getName();
				String actionId = null;

				final Action anonymousAction = EvaluableUtility.createAnonymousAction(handler, presenter);
				if (anonymousAction != null) {
					actionId = anonymousAction.getId();
					presenter.setAction(actionId, anonymousAction);
				}

				if (!Strings.isNullOrEmpty(actionId) && !Strings.isNullOrEmpty(name)) {
					callback.setComponentHandler(component, element, handler, name, actionId, anonymousAction,
							presenter);
				}
			}
		}
	}

}
