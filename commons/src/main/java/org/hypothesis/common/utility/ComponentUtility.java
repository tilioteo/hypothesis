/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.common.utility;

import java.util.Date;

import org.hypothesis.interfaces.AlignmentWrapper;
import org.hypothesis.interfaces.ComponentEvent;
import org.hypothesis.interfaces.ComponentEventCallback;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;

import com.tilioteo.common.collections.StringMap;
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
public class ComponentUtility {

	private ComponentUtility() {
	}

	/**
	 * Set common component properties
	 * 
	 * @param component
	 * @param element
	 * @param stringMap
	 * @param alignmentWrapper
	 */
	public static void setCommonProperties(Component component, Element element, StringMap stringMap,
			AlignmentWrapper alignmentWrapper) {
		// store component id
		if (component instanceof AbstractComponent)
			((AbstractComponent) component).setData(DocumentUtility.getId(element));

		setCaption(component, stringMap);

		setWidth(component, stringMap.getDimension(DocumentConstants.WIDTH));
		setHeight(component, stringMap.getDimension(DocumentConstants.HEIGHT));
		component.setVisible(stringMap.getBoolean(DocumentConstants.VISIBLE, true));
		component.setEnabled(stringMap.getBoolean(DocumentConstants.ENABLED, true));

		setStyle(component, stringMap);

		setWrappedAlignment(stringMap, alignmentWrapper);
	}

	private static void setStyle(Component component, StringMap properties) {
		if (component instanceof AbstractComponent) {
			String style = properties.get(DocumentConstants.STYLE);
			if (style != null) {
				component.addStyleName(style);
			}
		}
	}

	private static void setCaption(Component component, StringMap properties) {
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

	private static void setWrappedAlignment(StringMap properties, AlignmentWrapper alignmentWrapper) {
		if (alignmentWrapper != null) {
			String align = properties.get(DocumentConstants.ALIGNMENT);
			Alignment alignment = stringToAlignment(align);
			alignmentWrapper.setAlignment(alignment);
		}
	}

	private static Alignment stringToAlignment(String align) {
		if (align != null) {
			align = align.trim().toLowerCase();
			if ("tl".equalsIgnoreCase(align) || "lt".equalsIgnoreCase(align))
				return Alignment.TOP_LEFT;
			else if ("tc".equalsIgnoreCase(align) || "ct".equalsIgnoreCase(align))
				return Alignment.TOP_CENTER;
			else if ("tr".equalsIgnoreCase(align) || "rt".equalsIgnoreCase(align))
				return Alignment.TOP_RIGHT;
			else if ("ml".equalsIgnoreCase(align) || "lm".equalsIgnoreCase(align))
				return Alignment.MIDDLE_LEFT;
			else if ("mc".equalsIgnoreCase(align) || "cm".equalsIgnoreCase(align))
				return Alignment.MIDDLE_CENTER;
			else if ("mr".equalsIgnoreCase(align) || "rm".equalsIgnoreCase(align))
				return Alignment.MIDDLE_RIGHT;
			else if ("bl".equalsIgnoreCase(align) || "lb".equalsIgnoreCase(align))
				return Alignment.BOTTOM_LEFT;
			else if ("bc".equalsIgnoreCase(align) || "cb".equalsIgnoreCase(align))
				return Alignment.BOTTOM_CENTER;
			else if ("br".equalsIgnoreCase(align) || "rb".equalsIgnoreCase(align))
				return Alignment.BOTTOM_RIGHT;
		}

		// default
		return Alignment.MIDDLE_CENTER;
	}

	/**
	 * Set common fields properties
	 * 
	 * @param component
	 * @param element
	 * @param stringMap
	 * @param alignmentWrapper
	 */
	@SuppressWarnings("rawtypes")
	public static void setCommonFieldProperties(AbstractField component, Element element, StringMap stringMap,
			AlignmentWrapper alignmentWrapper) {
		setCommonProperties(component, element, stringMap, alignmentWrapper);

		// set AbstractField specific properties
		component.setReadOnly(stringMap.getBoolean(DocumentConstants.READ_ONLY, false));
	}

	/**
	 * Set common layout properties
	 * 
	 * @param component
	 * @param element
	 * @param stringMap
	 * @param alignmentWrapper
	 */
	public static void setCommonLayoutProperties(AbstractOrderedLayout component, Element element, StringMap stringMap,
			AlignmentWrapper alignmentWrapper) {
		setCommonProperties(component, element, stringMap, alignmentWrapper);
		setLayoutSpacing(component, stringMap);
	}

	private static void setLayoutSpacing(AbstractOrderedLayout component, StringMap properties) {
		// TODO how to handle with spacing value?
		// int value = stringMap.getInteger(SlideXmlConstants.SPACING, -1);
		// if (value > 0)
		boolean value = properties.getBoolean(DocumentConstants.SPACING, false);
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
		return new ComponentEventCallback() {
			@Override
			public void initEvent(ComponentEvent componentEvent) {
				componentEvent.setTimestamp(serverTimestamp);
				componentEvent.setClientTimestamp(clientTimestamp);
			}
		};
	}
}
