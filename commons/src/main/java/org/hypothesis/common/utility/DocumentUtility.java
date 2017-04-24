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
		return Optional.ofNullable(parent)
				.filter(f -> StringUtils.isNotBlank(startName)).map(m -> m.children().stream()
						.filter(f -> f.getName().startsWith(startName)).collect(Collectors.toList()))
				.orElse(Collections.emptyList());
	}

	public static Optional<String> getTrimmedText(Element element) {
		return Optional.ofNullable(element).map(m -> m.getText()).map(m -> m.trim());
	}

	public static List<Element> getSubElementChildren(Element element, String subNodeName,
			List<String> validElementNames) {
		return Optional.ofNullable(element).filter(f -> StringUtils.isNotEmpty(subNodeName))
				.map(m -> m.selectElement(subNodeName))
				.map(m -> m.children().stream()
						.filter(f -> null == validElementNames || validElementNames.contains(f.getName()))
						.collect(Collectors.toList()))
				.orElse(Collections.emptyList());
	}

	public static boolean isValidSlideDocument(Document document) {
		return Optional.ofNullable(document).map(m -> m.root()).filter(f -> DocumentConstants.SLIDE.equals(f.getName()))
				.isPresent();
	}

	public static Optional<String> getId(Element element) {
		return Optional.ofNullable(element).map(m -> m.getAttribute(DocumentConstants.ID));
	}

	public static Optional<String> getCaption(Element element) {
		return Optional.ofNullable(element).map(m -> m.getAttribute(DocumentConstants.CAPTION));
	}

	public static List<Element> getActionsElements(Element documentRoot) {
		return Optional.ofNullable(documentRoot)
				.filter(f -> ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(f.getName()))
				.map(m -> m.selectElement(DocumentConstants.ACTIONS))
				.map(m -> m.selectElements(DocumentConstants.ACTION)).orElse(Collections.emptyList());
	}

	public static Optional<Element> getExpressionElement(Element element) {
		return Optional.ofNullable(element).map(m -> m.selectElement(DocumentConstants.EXPRESSION));
	}

	public static Optional<Element> getTrueElement(Element element) {
		return Optional.ofNullable(element).map(m -> m.selectElement(DocumentConstants.TRUE));
	}

	public static Optional<Element> getFalseElement(Element element) {
		return Optional.ofNullable(element).map(m -> m.selectElement(DocumentConstants.FALSE));
	}

	public static Optional<Element> getLoopElement(Element element) {
		return Optional.ofNullable(element).map(m -> m.selectElement(DocumentConstants.LOOP));
	}

	public static List<Element> getCaseElements(Element element) {
		return element.selectElements(DocumentConstants.CASE);
	}

	public static Optional<String> getValue(Element element) {
		return Optional.ofNullable(element).map(m -> m.getAttribute(DocumentConstants.VALUE));
	}

	public static Optional<String> getAction(Element element) {
		return Optional.ofNullable(element).map(m -> m.getAttribute(DocumentConstants.ACTION));
	}

	public static Optional<String> getType(Element element) {
		return Optional.ofNullable(element).map(m -> m.getAttribute(DocumentConstants.TYPE));
	}

	public static Optional<String> getKey(Element element) {
		return Optional.ofNullable(element).map(m -> m.getAttribute(DocumentConstants.KEY));
	}

	public static Optional<String> getValues(Element element) {
		return Optional.ofNullable(element).map(m -> m.getAttribute(DocumentConstants.VALUES));
	}

	public static Optional<String> getName(Element element) {
		return Optional.ofNullable(element).map(m -> m.getAttribute(DocumentConstants.NAME));
	}

	public static Optional<String> getUid(Element element) {
		return Optional.ofNullable(element).map(m -> m.getAttribute(DocumentConstants.UID));
	}

	public static List<Element> getVariablesElements(Element documentRoot) {
		return Optional.ofNullable(documentRoot)
				.filter(f -> ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(f.getName()))
				.map(m -> m.selectElement(DocumentConstants.VARIABLES))
				.map(m -> m.selectElements(DocumentConstants.VARIABLE)).orElse(Collections.emptyList());
	}

	public static Optional<Element> getReferenceSubElement(Element element) {
		return Optional.ofNullable(element).map(m -> m.selectElement(DocumentConstants.REFERENCE))
				.map(m -> m.firstChild());
	}

	public static Optional<Element> getInstanceSubElement(Element element) {
		return Optional.ofNullable(element).map(m -> m.selectElement(DocumentConstants.INSTANCE))
				.map(m -> m.firstChild());
	}

	public static List<Element> getScoresElements(Element documentRoot) {
		return Optional.ofNullable(documentRoot)
				.map(m -> m.selectElement(DocumentConstants.SCORES))
				.map(m -> m.selectElements(DocumentConstants.SCORE)).orElse(Collections.emptyList());
	}

	public static List<Element> getInputValueElements(Element documentRoot) {
		return Optional.ofNullable(documentRoot)
				.filter(f -> ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(f.getName()))
				.map(m -> findElementsByNameStarting(m, DocumentConstants.INPUT_VALUE)).orElse(Collections.emptyList());
	}

	public static List<Element> getOutputValueElements(Element documentRoot) {
		return Optional.ofNullable(documentRoot)
				.filter(f -> ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(f.getName()))
				.map(m -> findElementsByNameStarting(m, DocumentConstants.OUTPUT_VALUE))
				.orElse(Collections.emptyList());
	}

	public static List<Element> getTimersElements(Element documentRoot) {
		return Optional.ofNullable(documentRoot)
				.filter(f -> ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(f.getName()))
				.map(m -> m.selectElement(DocumentConstants.TIMERS)).map(m -> m.selectElements(DocumentConstants.TIMER))
				.orElse(Collections.emptyList());
	}

	public static Map<String, String> getPropertyValueMap(Element component) {
		return getComponentProperties(component).stream()
				.collect(Collectors.toMap(k -> k.getName(), v -> v.getAttribute(DocumentConstants.VALUE)));
	}

	public static List<Element> getComponentValidators(Element field) {
		return getSubElementChildren(field, DocumentConstants.VALIDATORS, null);
	}

	public static List<Element> getComponentProperties(Element component) {
		return getSubElementChildren(component, DocumentConstants.PROPERTIES, null);
	}

	public static List<Element> getContainerComponents(Element container, List<String> valids) {
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
		return Optional.ofNullable(documentRoot)
				.filter(f -> ValidationSets.VALID_SLIDE_ROOT_ELEMENTS.contains(f.getName()))
				.map(m -> m.selectElement(DocumentConstants.WINDOWS))
				.map(m -> m.selectElements(DocumentConstants.WINDOW)).orElse(Collections.emptyList());
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
		return getMessageElement(element).flatMap(DocumentUtility::getTrimmedText).filter(StringUtils::isNotBlank)
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
		return Optional.ofNullable(document).map(m -> m.root())
				.filter(f -> DocumentConstants.BRANCH.equals(f.getName())).isPresent();
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

	public static Optional<Long> getSlideId(Element element) {
		return Optional.ofNullable(element).map(m -> m.getAttribute(DocumentConstants.SLIDE_ID))
				.filter(StringUtils::isNotEmpty).map(m -> {
					try {
						return Long.parseLong(m);
					} catch (NumberFormatException e) {
						e.printStackTrace();
						return null;
					}
				});
	}

	public static boolean isValidTaskDocument(Document document) {
		return Optional.ofNullable(document).map(m -> m.root()).filter(f -> DocumentConstants.TASK.equals(f.getName()))
				.isPresent();
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
		return Optional.ofNullable(document).map(m -> m.root())
				.filter(f -> DocumentConstants.MESSAGE.equals(f.getName())).isPresent();
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

			final Action anonymousAction = EvaluableUtility.createAnonymousAction(e, presenter).orElse(null);
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
