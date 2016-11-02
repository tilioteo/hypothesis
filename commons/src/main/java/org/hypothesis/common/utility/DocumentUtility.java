/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.common.utility;

import com.tilioteo.common.Strings;
import com.tilioteo.common.collections.StringMap;
import com.tilioteo.common.collections.StringSet;
import com.vaadin.ui.Component;
import org.apache.commons.lang3.StringUtils;
import org.hypothesis.common.ValidationSets;
import org.hypothesis.interfaces.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
	public static Element findElementByNameAndValue(Element element, String name, Map<String, String> attributes,
			boolean descendant) {
		if (element != null) {
			List<Element> elements = element.children();

			for (Element selected : elements) {
				if (selected.getName().equals(name)) {
					if (attributes != null && !attributes.isEmpty()) {
						boolean passed = true;
						final Map<String, String> selectedAttributes = selected.attributes();
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
					return findElementByNameAndValue(selected, name, attributes, descendant);
				}
			}
		}

		return null;
	}

	public static List<Element> findElementsByNameStarting(Element parent, String startName) {
		if (parent != null && startName.length() > 0) {
			return parent.children().stream().filter(f -> f.getName().startsWith(startName))
					.collect(Collectors.toList());
		}

		return Collections.emptyList();
	}

	public static String getTrimmedText(Element element) {
		if (element != null && element.getText() != null) {
			return element.getText().trim();
		}

		return null;
	}

	public static List<Element> getSubElementChildren(Element element, String subNodeName,
			StringSet validElementNames) {
		if (element != null && StringUtils.isNotEmpty(subNodeName)) {
			Element subElement = element.selectElement(subNodeName);

			if (subElement != null) {
				return subElement.children().stream()
						.filter(f -> null == validElementNames || validElementNames.contains(f.getName()))
						.collect(Collectors.toList());
			}
		}

		return Collections.emptyList();
	}

	public static boolean isValidSlideDocument(Document doc) {
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
				return Collections.emptyList();
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Element element = documentRoot.selectElement(DocumentConstants.ACTIONS);
			if (element != null) {
				return element.selectElements(DocumentConstants.ACTION);
			}
		}

		return Collections.emptyList();
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
				return Collections.emptyList();
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Element element = documentRoot.selectElement(DocumentConstants.VARIABLES);
			if (element != null) {
				return element.selectElements(DocumentConstants.VARIABLE);
			}
		}

		return Collections.emptyList();
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
				return Collections.emptyList();
				// throw new NotValidDocumentRoot(documentRoot);
			}

			return findElementsByNameStarting(documentRoot, DocumentConstants.INPUT_VALUE);
		}

		return Collections.emptyList();
	}

	public static List<Element> getOutputValueElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return Collections.emptyList();
				// throw new NotValidDocumentRoot(documentRoot);
			}

			return findElementsByNameStarting(documentRoot, DocumentConstants.OUTPUT_VALUE);
		}

		return Collections.emptyList();
	}

	public static List<Element> getTimersElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return Collections.emptyList();
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Element element = documentRoot.selectElement(DocumentConstants.TIMERS);
			if (element != null) {
				return element.selectElements(DocumentConstants.TIMER);
			}
		}

		return Collections.emptyList();
	}

	public static StringMap getPropertyValueMap(Element component) {
		StringMap map = new StringMap();
		getComponentProperties(component).forEach(e -> map.put(e.getName(), e.getAttribute(DocumentConstants.VALUE)));

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

		return Collections.emptyList();
	}

	public static List<Element> getWindowsElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(documentRoot.getName())) {
				return Collections.emptyList();
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Element element = documentRoot.selectElement(DocumentConstants.WINDOWS);
			if (element != null) {
				return element.selectElements(DocumentConstants.WINDOW);
			}
		}

		return Collections.emptyList();
	}

	public static Element getViewportOrWindowRootElement(Element element) {
		if (element != null) {
			if (element.getName().equals(DocumentConstants.VIEWPORT)
					|| element.getName().equals(DocumentConstants.WINDOW)) {
				return element;
			}

			return element.children().stream().filter(f -> ValidationSets.VALID_VIEWPORT_CHILDREN.contains(f.getName()))
					.findFirst().orElse(null);
			// throw new ViewportNotValidElementFound(viewport);
		}

		return null;
	}

	public static Element getViewportInnerComponent(Element documentRoot) {
		Element viewportRootElement = getVieportRootElement(documentRoot);

		if (viewportRootElement != null) {
			return viewportRootElement.children().stream()
					.filter(f -> ValidationSets.VALID_VIEWPORT_CHILDREN.contains(f.getName())).findFirst().orElse(null);
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
				return Collections.emptyList();
				// throw new NotValidDocumentRoot(documentRoot);
			}

			return documentRoot.selectElements(DocumentConstants.PATH);
		}

		return Collections.emptyList();
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

		return Collections.emptyList();
	}

	public static Long getSlideId(Element element) {
		if (element != null) {
			String idString = element.getAttribute(DocumentConstants.SLIDE_ID);
			if (StringUtils.isNotEmpty(idString)) {
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

		return Collections.emptyList();
	}

	public static Element getEvaluateElement(Element element) {
		if (element != null) {
			return element.selectElement(DocumentConstants.EVALUATE);
		}

		return null;
	}

	public static boolean isValidMessageDocument(Document document) {
		return document != null && document.root() != null
				&& document.root().getName().equals(DocumentConstants.MESSAGE);
	}

	public static List<Element> getPropertyElements(Element documentRoot) {
		if (documentRoot != null) {
			if (!DocumentConstants.MESSAGE.equals(documentRoot.getName())) {
				return Collections.emptyList();
				// throw new NotValidDocumentRoot(documentRoot);
			}

			Element element = documentRoot.selectElement(DocumentConstants.PROPERTIES);
			if (element != null) {
				return element.selectElements(DocumentConstants.PROPERTY);
			}
		}

		return Collections.emptyList();
	}

	public static void iterateHandlers(Component component, Element element, SlidePresenter presenter,
			HandlerCallback callback) {
		DocumentUtility.getComponentHandlers(element).forEach(e -> {
			String name = e.getName();
			String actionId = null;

			final Action anonymousAction = EvaluableUtility.createAnonymousAction(e, presenter);
			if (anonymousAction != null) {
				actionId = anonymousAction.getId();
				presenter.setAction(actionId, anonymousAction);
			}

			if (StringUtils.isNotEmpty(actionId) && StringUtils.isNotEmpty(name)) {
				callback.setComponentHandler(component, element, e, name, actionId, anonymousAction, presenter);
			}
		});
	}

}
