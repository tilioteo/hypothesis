/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.common.utility;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
	public static Optional<Element> findElementByNameAndValue(Element element, String name,
			Map<String, String> attributes, boolean descendant) {
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
							return Optional.ofNullable(selected);
						}
					} else {
						return Optional.ofNullable(selected);
					}
				} else if (descendant) {
					return findElementByNameAndValue(selected, name, attributes, descendant);
				}
			}
		}

		return Optional.empty();
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

	public static Optional<Element> getExpressionElement(Element element) {
		if (element != null) {
			return Optional.ofNullable(element.selectElement(DocumentConstants.EXPRESSION));
		}

		return Optional.empty();
	}

	public static Optional<Element> getTrueElement(Element element) {
		if (element != null) {
			return Optional.ofNullable(element.selectElement(DocumentConstants.TRUE));
		}

		return Optional.empty();
	}

	public static Optional<Element> getFalseElement(Element element) {
		if (element != null) {
			return Optional.ofNullable(element.selectElement(DocumentConstants.FALSE));
		}

		return Optional.empty();
	}

	public static Optional<Element> getLoopElement(Element element) {
		if (element != null) {
			return Optional.ofNullable(element.selectElement(DocumentConstants.LOOP));
		}

		return Optional.empty();
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

	public static Optional<Element> getReferenceSubElement(Element element) {
		if (element != null) {
			Element reference = element.selectElement(DocumentConstants.REFERENCE);
			if (reference != null) {
				return Optional.ofNullable(reference.firstChild());
			}
		}

		return Optional.empty();
	}

	public static Optional<Element> getInstanceSubElement(Element element) {
		if (element != null) {
			Element instance = element.selectElement(DocumentConstants.INSTANCE);
			if (instance != null) {
				return Optional.ofNullable(instance.firstChild());
			}
		}

		return Optional.empty();
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

	public static List<Element> getComponentSources(Element element) {
		return Optional.ofNullable(element).map(m -> m.selectElement(DocumentConstants.SOURCES))
				.flatMap(m -> Optional.ofNullable(m.selectElements(DocumentConstants.SOURCE)))
				.orElse(Collections.emptyList());
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

	public static Optional<Element> getViewportOrWindowRootElement(Element element) {
		return Optional.ofNullable(element)
				.flatMap(m -> Arrays.asList(DocumentConstants.VIEWPORT, DocumentConstants.WINDOW).contains(m.getName())
						? Optional.of(m)
						: m.children().stream()
								.filter(f -> ValidationSets.VALID_VIEWPORT_CHILDREN.contains(f.getName())).findFirst());
	}

	public static Optional<Element> getViewportInnerComponent(Element documentRoot) {
		return getVieportRootElement(documentRoot).flatMap(m -> m.children().stream()
				.filter(f -> ValidationSets.VALID_VIEWPORT_CHILDREN.contains(f.getName())).findFirst());
	}

	public static Optional<Element> getVieportRootElement(Element documentRoot) {
		return getViewportElement(documentRoot).flatMap(DocumentUtility::getViewportOrWindowRootElement);
	}

	private static Optional<Element> getViewportElement(Element documentRoot) {
		return Optional.ofNullable(documentRoot)
				.filter(f -> ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(f.getName()))
				.flatMap(m -> findElementByNameAndValue(m, DocumentConstants.VIEWPORT, null, true));
	}

	public static String getValidatorMessage(Element element, String defaultMessage) {
		return getMessageElement(element).map(DocumentUtility::getTrimmedText).filter(StringUtils::isNotBlank)
				.orElse(defaultMessage);
	}

	public static Optional<Element> getMessageElement(Element element) {
		return Optional.ofNullable(element).map(m -> m.selectElement(DocumentConstants.MESSAGE));
	}

	public static Double getNumberValidatorMinValue(Element element) {
		return getMinElement(element).map(m -> m.getAttribute(DocumentConstants.VALUE)).map(Strings::toDouble)
				.orElse(null);
	}

	public static Double getNumberValidatorMaxValue(Element element) {
		return getMaxElement(element).map(m -> m.getAttribute(DocumentConstants.VALUE)).map(Strings::toDouble)
				.orElse(null);
	}

	public static Optional<Element> getMinElement(Element element) {
		return Optional.ofNullable(element.selectElement(DocumentConstants.MIN));
	}

	public static Optional<Element> getMaxElement(Element element) {
		return Optional.ofNullable(element.selectElement(DocumentConstants.MAX));
	}

	public static Date getDateValidatorMinValue(Element element, String defaultFormat) {
		return getMinElement(
				element).map(
						m -> Strings
								.toDate(m.getAttribute(DocumentConstants.VALUE),
										StringUtils.isNotBlank(m.getAttribute(DocumentConstants.FORMAT))
												? m.getAttribute(DocumentConstants.FORMAT) : defaultFormat))
						.orElse(null);
	}

	public static Date getDateValidatorMaxValue(Element element, String defaultFormat) {
		return getMaxElement(
				element).map(
						m -> Strings
								.toDate(m.getAttribute(DocumentConstants.VALUE),
										StringUtils.isNotBlank(m.getAttribute(DocumentConstants.FORMAT))
												? m.getAttribute(DocumentConstants.FORMAT) : defaultFormat))
						.orElse(null);
	}

	public static boolean isValidBranchDocument(Document document) {
		return document != null && document.root() != null
				&& document.root().getName().equals(DocumentConstants.BRANCH);
	}

	public static List<Element> getPathElements(Element documentRoot) {
		return Optional.ofNullable(documentRoot).filter(f -> DocumentConstants.BRANCH.equals(f.getName()))
				.flatMap(m -> Optional.ofNullable(m.selectElements(DocumentConstants.PATH)))
				.orElse(Collections.emptyList());
	}

	public static Optional<Element> getDefaultPathElement(Element documentRoot) {
		return Optional.ofNullable(documentRoot).filter(f -> DocumentConstants.BRANCH.equals(f.getName()))
				.flatMap(m -> findElementByNameAndValue(m, DocumentConstants.DEFAULT_PATH, null, true));
	}

	public static Optional<Element> getBranchKeyElement(Element element) {
		return Optional.ofNullable(element)
				.flatMap(m -> DocumentUtility.findElementByNameAndValue(m, DocumentConstants.BRANCH_KEY, null, true));
	}

	public static Optional<Element> getPatternElement(Element element) {
		return Optional.ofNullable(element)
				.flatMap(m -> DocumentUtility.findElementByNameAndValue(m, DocumentConstants.PATTERN, null, true));
	}

	public static List<Element> getNickElements(Element element) {
		return Optional.ofNullable(element).flatMap(m -> Optional.ofNullable(m.selectElements(DocumentConstants.NICK)))
				.orElse(Collections.emptyList());
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
		return document != null && document.root() != null && document.root().getName().equals(DocumentConstants.TASK);
	}

	public static List<Element> getNodesElements(Element element) {
		return Optional.ofNullable(element).map(m -> m.selectElement(DocumentConstants.NODES))
				.flatMap(m -> Optional.ofNullable(m.selectElements(DocumentConstants.NODE)))
				.orElse(Collections.emptyList());
	}

	public static Optional<Element> getEvaluateElement(Element element) {
		return Optional.ofNullable(element)
				.flatMap(m -> Optional.ofNullable(element.selectElement(DocumentConstants.EVALUATE)));
	}

	public static boolean isValidMessageDocument(Document document) {
		return document != null && document.root() != null
				&& document.root().getName().equals(DocumentConstants.MESSAGE);
	}

	public static List<Element> getMessagePropertyElements(Element documentRoot) {
		return Optional.ofNullable(documentRoot).filter(f -> DocumentConstants.MESSAGE.equals(f.getName()))
				.map(m -> m.selectElement(DocumentConstants.PROPERTIES))
				.flatMap(m -> Optional.ofNullable(m.selectElements(DocumentConstants.PROPERTY)))
				.orElse(Collections.emptyList());
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
