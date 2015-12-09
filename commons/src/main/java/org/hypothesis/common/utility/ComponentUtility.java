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

		setWrappedAlignment(stringMap, alignmentWrapper);
	}

	private static void setCaption(Component component, StringMap properties) {
		if (component instanceof AbstractComponent) {
			String caption = properties.get(DocumentConstants.CAPTION);
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
			if (align.equals("tl") || align.equals("lt"))
				return Alignment.TOP_LEFT;
			else if (align.equals("tc") || align.equals("ct"))
				return Alignment.TOP_CENTER;
			else if (align.equals("tr") || align.equals("rt"))
				return Alignment.TOP_RIGHT;
			else if (align.equals("ml") || align.equals("lm"))
				return Alignment.MIDDLE_LEFT;
			else if (align.equals("mc") || align.equals("cm"))
				return Alignment.MIDDLE_CENTER;
			else if (align.equals("mr") || align.equals("rm"))
				return Alignment.MIDDLE_RIGHT;
			else if (align.equals("bl") || align.equals("lb"))
				return Alignment.BOTTOM_LEFT;
			else if (align.equals("bc") || align.equals("cb"))
				return Alignment.BOTTOM_CENTER;
			else if (align.equals("br") || align.equals("rb"))
				return Alignment.BOTTOM_RIGHT;
		}

		// default
		return Alignment.MIDDLE_CENTER;
	}

	@SuppressWarnings("rawtypes")
	public static void setCommonFieldProperties(AbstractField component, Element element, StringMap stringMap,
			AlignmentWrapper alignmentWrapper) {
		setCommonProperties(component, element, stringMap, alignmentWrapper);

		// set AbstractField specific properties
		component.setReadOnly(stringMap.getBoolean(DocumentConstants.READ_ONLY, false));
	}

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

	public static ComponentEventCallback createDefaultEventCallback(final Date serverTimestamp,
			final Date clientTimestamp) {
		return new ComponentEventCallback() {
			@Override
			public void initEvent(ComponentEvent componentEvent) {
				componentEvent.setTimestamp(new Date());
				componentEvent.setClientTimestamp(clientTimestamp);
			}
		};
	}

}
