/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.common.utility;

import static com.vaadin.ui.Alignment.BOTTOM_CENTER;
import static com.vaadin.ui.Alignment.BOTTOM_LEFT;
import static com.vaadin.ui.Alignment.BOTTOM_RIGHT;
import static com.vaadin.ui.Alignment.MIDDLE_CENTER;
import static com.vaadin.ui.Alignment.MIDDLE_LEFT;
import static com.vaadin.ui.Alignment.MIDDLE_RIGHT;
import static com.vaadin.ui.Alignment.TOP_CENTER;
import static com.vaadin.ui.Alignment.TOP_LEFT;
import static com.vaadin.ui.Alignment.TOP_RIGHT;
import static org.hypothesis.common.utility.StringUtility.getBoolean;
import static org.hypothesis.common.utility.StringUtility.getDimension;
import static org.hypothesis.interfaces.DocumentConstants.ALIGNMENT;
import static org.hypothesis.interfaces.DocumentConstants.CAPTION;
import static org.hypothesis.interfaces.DocumentConstants.ENABLED;
import static org.hypothesis.interfaces.DocumentConstants.HEIGHT;
import static org.hypothesis.interfaces.DocumentConstants.READ_ONLY;
import static org.hypothesis.interfaces.DocumentConstants.SPACING;
import static org.hypothesis.interfaces.DocumentConstants.STYLE;
import static org.hypothesis.interfaces.DocumentConstants.VISIBLE;
import static org.hypothesis.interfaces.DocumentConstants.WIDTH;

import java.util.Date;
import java.util.Map;

import org.hypothesis.interfaces.AlignmentWrapper;
import org.hypothesis.interfaces.ComponentEvent;
import org.hypothesis.interfaces.ComponentEventCallback;
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
public class ComponentUtility {

	public static void setCommonProperties(Component component, Element element, Map<String, String> stringMap,
			AlignmentWrapper alignmentWrapper) {
		// store component id
		if (component instanceof AbstractComponent)
			((AbstractComponent) component).setData(DocumentUtility.getId(element));

		setCaption(component, stringMap);

		setWidth(component, getDimension(stringMap, WIDTH));
		setHeight(component, getDimension(stringMap, HEIGHT));
		component.setVisible(getBoolean(stringMap, VISIBLE, true));
		component.setEnabled(getBoolean(stringMap, ENABLED, true));

		setStyle(component, stringMap);

		setWrappedAlignment(stringMap, alignmentWrapper);
	}

	private static void setStyle(Component component, Map<String, String> properties) {
		if (component instanceof AbstractComponent) {
			String style = properties.get(STYLE);
			if (style != null) {
				component.addStyleName(style);
			}
		}
	}

	private static void setCaption(Component component, Map<String, String> properties) {
		if (component instanceof AbstractComponent) {
			String caption = properties.get(CAPTION);
			if (caption != null) {
				component.setCaption(caption);
			}
		}
	}

	public static void setWidth(Component component, String dimension) {
		if (component != null) {
			component.setWidth(dimension);
		}
	}

	public static void setHeight(Component component, String dimension) {
		if (component != null) {
			component.setHeight(dimension);
		}
	}

	private static void setWrappedAlignment(Map<String, String> properties, AlignmentWrapper alignmentWrapper) {
		if (alignmentWrapper != null) {
			String align = properties.get(ALIGNMENT);
			Alignment alignment = stringToAlignment(align);
			alignmentWrapper.setAlignment(alignment);
		}
	}

	private static Alignment stringToAlignment(String align) {
		if (align != null) {
			align = align.trim().toLowerCase();
			if (align.equals("tl") || align.equals("lt"))
				return TOP_LEFT;
			else if (align.equals("tc") || align.equals("ct"))
				return TOP_CENTER;
			else if (align.equals("tr") || align.equals("rt"))
				return TOP_RIGHT;
			else if (align.equals("ml") || align.equals("lm"))
				return MIDDLE_LEFT;
			else if (align.equals("mc") || align.equals("cm"))
				return MIDDLE_CENTER;
			else if (align.equals("mr") || align.equals("rm"))
				return MIDDLE_RIGHT;
			else if (align.equals("bl") || align.equals("lb"))
				return BOTTOM_LEFT;
			else if (align.equals("bc") || align.equals("cb"))
				return BOTTOM_CENTER;
			else if (align.equals("br") || align.equals("rb"))
				return BOTTOM_RIGHT;
		}

		// default
		return MIDDLE_CENTER;
	}

	@SuppressWarnings("rawtypes")
	public static void setCommonFieldProperties(AbstractField component, Element element, Map<String, String> stringMap,
			AlignmentWrapper alignmentWrapper) {
		setCommonProperties(component, element, stringMap, alignmentWrapper);

		// set AbstractField specific properties
		component.setReadOnly(getBoolean(stringMap, READ_ONLY, false));
	}

	public static void setCommonLayoutProperties(AbstractOrderedLayout component, Element element,
			Map<String, String> stringMap, AlignmentWrapper alignmentWrapper) {
		setCommonProperties(component, element, stringMap, alignmentWrapper);
		setLayoutSpacing(component, stringMap);
	}

	private static void setLayoutSpacing(AbstractOrderedLayout component, Map<String, String> properties) {
		// TODO how to handle with spacing value?
		// int value = stringMap.getInteger(SlideXmlConstants.SPACING, -1);
		// if (value > 0)
		boolean value = getBoolean(properties, SPACING, false);
		component.setSpacing(value);
	}

	public static ComponentEventCallback createDefaultEventCallback(final Date serverTimestamp,
			final Date clientTimestamp) {
		return componentEvent -> {
			componentEvent.setTimestamp(new Date());
			componentEvent.setClientTimestamp(clientTimestamp);
		};
	}
}
