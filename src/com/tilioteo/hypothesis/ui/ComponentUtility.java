/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.ui.MultipleComponentPanel.Orientation;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractMedia;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ComponentUtility {

	private static Orientation getOrientation(StringMap properties,
			Orientation defaultOrientation) {
		String orientation = properties.get(SlideXmlConstants.ORIENTATION);
		if (orientation != null) {
			if ("horizontal".equals(orientation.toLowerCase()))
				return Orientation.Horizontal;
			else if ("vertical".equals(orientation.toLowerCase()))
				return Orientation.Vertical;
		}
		return defaultOrientation;
	}

	private static void setCaption(Component component, StringMap properties) {
		if (component instanceof AbstractComponent) {
			String caption = properties.get(SlideXmlConstants.CAPTION);
			if (caption != null) {
				component.setCaption(caption);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static void setCommonFieldProperties(AbstractField component,
			Element element, StringMap stringMap,
			ParentAlignment parentAlignment) {
		setCommonProperties(component, element, stringMap, parentAlignment);

		// set AbstractField specific properties
		component.setReadOnly(stringMap.getBoolean(SlideXmlConstants.READ_ONLY, false));
	}

	public static void setCommonLayoutProperties(
			AbstractOrderedLayout component, Element element,
			StringMap stringMap, ParentAlignment parentAlignment) {
		setCommonProperties(component, element, stringMap, parentAlignment);
		setLayoutSpacing(component, stringMap);
	}

	public static void setCommonProperties(Component component,
			Element element, StringMap stringMap,
			ParentAlignment parentAlignment) {
		// store component id
		if (component instanceof AbstractComponent)
			((AbstractComponent) component).setData(SlideXmlUtility.getId(element));

		setCaption(component, stringMap);

		setWidth(component, stringMap.getDimension(SlideXmlConstants.WIDTH));
		setHeight(component, stringMap.getDimension(SlideXmlConstants.HEIGHT));
		component.setVisible(stringMap.getBoolean(SlideXmlConstants.VISIBLE, true));
		component.setEnabled(stringMap.getBoolean(SlideXmlConstants.ENABLED, true));

		setParentAlignment(stringMap, parentAlignment);
	}

	public static void setComponentPanelProperties(
			MultipleComponentPanel<? extends AbstractComponent> component,
			Element element, StringMap stringMap,
			ParentAlignment parentAlignment) {
		setCommonProperties(component, element, stringMap, parentAlignment);

		Orientation orientation = getOrientation(stringMap,
				Orientation.Horizontal);
		component.setOrientation(orientation);
		
		setChildsSize(component, stringMap);
	}
	
	public static void setMediaSources(AbstractMedia media, Element component) {
		List<Element> elements = SlideXmlUtility.getComponentSources(component);
		
		List<Resource> resources = new ArrayList<Resource>();
		for (Element element : elements) {
			String url = element.attributeValue(SlideXmlConstants.URL);
			if (!Strings.isNullOrEmpty(url)) {
				resources.add(new ExternalResource(url));
			}
		}
		
		if (!resources.isEmpty()) {
			media.setSources(resources.toArray(new Resource[0]));
		}
	}

	private static void setChildsSize(
			MultipleComponentPanel<? extends AbstractComponent> component,
			StringMap stringMap) {
		setChildsWidth(component, stringMap.getDimension(SlideXmlConstants.CHILD_WIDTH));
		setChildsHeight(component, stringMap.getDimension(SlideXmlConstants.CHILD_HEIGHT));
		
	}

	private static void setChildsWidth(
			MultipleComponentPanel<? extends AbstractComponent> component,
			String dimension) {
		if (component != null) {
			component.setChildsWidth(dimension);
		}
	}

	private static void setChildsHeight(
			MultipleComponentPanel<? extends AbstractComponent> component,
			String dimension) {
		if (component != null) {
			component.setChildsHeight(dimension);
		}
	}

	public static void setHeight(Component component, String dimension) {
		if (component != null) {
			component.setHeight(dimension);
		}
	}

	private static void setLayoutSpacing(AbstractOrderedLayout component,
			StringMap properties) {
		// TODO how to handle with spacing value?
		// int value = stringMap.getInteger(SlideXmlConstants.SPACING, -1);
		// if (value > 0)
		boolean value = properties.getBoolean(SlideXmlConstants.SPACING, false);
		component.setSpacing(value);
	}

	private static void setParentAlignment(StringMap properties,
			ParentAlignment parentAlignment) {
		if (parentAlignment != null) {
			String align = properties.get(SlideXmlConstants.ALIGNMENT);
			Alignment alignment = stringToAlignment(align);
			parentAlignment.setAlignment(alignment);
		}
	}

	public static void setWidth(Component component, String dimension) {
		if (component != null) {
			component.setWidth(dimension);
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

		// return null;
		// default
		return Alignment.MIDDLE_CENTER;
	}

}
