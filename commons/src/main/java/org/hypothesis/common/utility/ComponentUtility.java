/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.common.utility;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hypothesis.interfaces.AlignmentWrapper;
import org.hypothesis.interfaces.ComponentEventCallback;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public final class ComponentUtility {

	private ComponentUtility() {
	}

	/**
	 * Set common component properties
	 * 
	 * @param component
	 * @param element
	 * @param properties
	 * @param alignmentWrapper
	 */
	public static void setCommonProperties(Component component, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		// store component id
		if (component instanceof AbstractComponent) {
			DocumentUtility.getId(element).filter(StringUtils::isNotEmpty)
					.ifPresent(((AbstractComponent) component)::setData);
		}

		setCaption(component, properties);

		setWidth(component, ConversionUtility.getDimension(properties.get(DocumentConstants.WIDTH)));
		setHeight(component, ConversionUtility.getDimension(properties.get(DocumentConstants.HEIGHT)));
		component.setVisible(ConversionUtility.getBooleanOrDefault(properties.get(DocumentConstants.VISIBLE), true));
		component.setEnabled(ConversionUtility.getBooleanOrDefault(properties.get(DocumentConstants.ENABLED), true));

		setStyle(component, properties);

		setWrappedAlignment(properties, alignmentWrapper);
	}

	private static void setStyle(Component component, Map<String, String> properties) {
		if (component instanceof AbstractComponent) {
			String style = properties.get(DocumentConstants.STYLE);
			if (style != null) {
				component.addStyleName(style);
			}
		}
	}

	private static void setCaption(Component component, Map<String, String> properties) {
		if (component instanceof AbstractComponent) {
			String caption = properties.get(DocumentConstants.CAPTION);
			if (caption != null) {
				component.setCaption(caption);
			}
		}
	}

	/**
	 * Set component width
	 * 
	 * @param component
	 * @param dimension
	 */
	public static void setWidth(Component component, String dimension) {
		if (component != null) {
			component.setWidth(dimension);
		}
	}

	/**
	 * Set component height
	 * 
	 * @param component
	 * @param dimension
	 */
	public static void setHeight(Component component, String dimension) {
		if (component != null) {
			component.setHeight(dimension);
		}
	}

	private static void setWrappedAlignment(Map<String, String> properties, AlignmentWrapper alignmentWrapper) {
		if (alignmentWrapper != null) {
			String align = properties.get(DocumentConstants.ALIGNMENT);
			Alignment alignment = stringToAlignment(align);
			alignmentWrapper.setAlignment(alignment);
		}
	}

	private static Alignment stringToAlignment(String align) {
		if (align != null) {
			switch (align.trim().toLowerCase()) {
			case "tl":
			case "lt":
				return Alignment.TOP_LEFT;
			case "tc":
			case "ct":
				return Alignment.TOP_CENTER;
			case "tr":
			case "rt":
				return Alignment.TOP_RIGHT;
			case "ml":
			case "lm":
				return Alignment.MIDDLE_LEFT;
			case "mc":
			case "cm":
				return Alignment.MIDDLE_CENTER;
			case "mr":
			case "rm":
				return Alignment.MIDDLE_RIGHT;
			case "bl":
			case "lb":
				return Alignment.BOTTOM_LEFT;
			case "bc":
			case "cb":
				return Alignment.BOTTOM_CENTER;
			case "br":
			case "rb":
				return Alignment.BOTTOM_RIGHT;
			default:
			}
		}

		// default
		return Alignment.MIDDLE_CENTER;
	}

	/**
	 * Set common fields properties
	 * 
	 * @param component
	 * @param element
	 * @param properties
	 * @param alignmentWrapper
	 */
	@SuppressWarnings("rawtypes")
	public static void setCommonFieldProperties(AbstractField component, Element element,
			Map<String, String> properties, AlignmentWrapper alignmentWrapper) {
		setCommonProperties(component, element, properties, alignmentWrapper);

		// set AbstractField specific properties
		component
				.setReadOnly(ConversionUtility.getBooleanOrDefault(properties.get(DocumentConstants.READ_ONLY), false));
	}

	/**
	 * Set common layout properties
	 * 
	 * @param component
	 * @param element
	 * @param stringMap
	 * @param alignmentWrapper
	 */
	public static void setCommonLayoutProperties(AbstractOrderedLayout component, Element element,
			Map<String, String> stringMap, AlignmentWrapper alignmentWrapper) {
		setCommonProperties(component, element, stringMap, alignmentWrapper);
		setLayoutSpacing(component, stringMap);
	}

	private static void setLayoutSpacing(AbstractOrderedLayout component, Map<String, String> properties) {
		// TODO how to handle with spacing value?
		// int value = stringMap.getInteger(SlideXmlConstants.SPACING, -1);
		// if (value > 0)
		boolean value = ConversionUtility.getBooleanOrDefault(properties.get(DocumentConstants.SPACING), false);
		component.setSpacing(value);
	}

	/**
	 * Create default event callback which sets server and client time of event
	 * only
	 * 
	 * @param serverTimestamp
	 * @param clientTimestamp
	 * @return
	 */
	public static ComponentEventCallback createDefaultEventCallback(final Date serverTimestamp,
			final Date clientTimestamp) {
		return e -> {
			e.setTimestamp(serverTimestamp);
			e.setClientTimestamp(clientTimestamp);
		};
	}
}
