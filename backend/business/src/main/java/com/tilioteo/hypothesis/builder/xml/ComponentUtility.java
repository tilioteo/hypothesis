/**
 * 
 */
package com.tilioteo.hypothesis.builder.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.vaadin.special.shared.ui.selectbutton.SelectButtonState.LabelPosition;
import org.vaadin.special.shared.ui.timer.TimerState.Direction;
import org.vaadin.special.ui.MultipleComponentPanel;
import org.vaadin.special.ui.MultipleComponentPanel.Orientation;

import com.tilioteo.common.Strings;
import com.tilioteo.common.collections.StringMap;
import com.tilioteo.hypothesis.builder.AlignmentWrapper;
import com.tilioteo.hypothesis.builder.BuilderConstants;
import com.tilioteo.hypothesis.slide.ui.Audio;
import com.tilioteo.hypothesis.slide.ui.Button;
import com.tilioteo.hypothesis.slide.ui.ButtonPanel;
import com.tilioteo.hypothesis.slide.ui.ComboBox;
import com.tilioteo.hypothesis.slide.ui.DateField;
import com.tilioteo.hypothesis.slide.ui.Image;
import com.tilioteo.hypothesis.slide.ui.Label;
import com.tilioteo.hypothesis.slide.ui.Panel;
import com.tilioteo.hypothesis.slide.ui.SelectPanel;
import com.tilioteo.hypothesis.slide.ui.TextArea;
import com.tilioteo.hypothesis.slide.ui.TextField;
import com.tilioteo.hypothesis.slide.ui.Timer;
import com.tilioteo.hypothesis.slide.ui.TimerLabel;
import com.tilioteo.hypothesis.slide.ui.Video;
import com.tilioteo.hypothesis.slide.ui.Window;
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
@SuppressWarnings("serial")
public class ComponentUtility implements Serializable {

	private static Orientation getOrientation(StringMap properties, Orientation defaultValue) {
		String value = properties.get(BuilderConstants.ORIENTATION);

		if (value != null) {
			if ("horizontal".equalsIgnoreCase(value))
				return Orientation.Horizontal;
			else if ("vertical".equalsIgnoreCase(value))
				return Orientation.Vertical;
		}

		return defaultValue;
	}

	private static LabelPosition getLabelPosition(StringMap properties, LabelPosition defaultValue) {
		String value = properties.get(BuilderConstants.LABEL_POSITION);

		if (value != null) {
			if ("right".equalsIgnoreCase(value))
				return LabelPosition.Right;
			else if ("left".equalsIgnoreCase(value))
				return LabelPosition.Left;
			else if ("bottom".equalsIgnoreCase(value))
				return LabelPosition.Bottom;
			else if ("top".equalsIgnoreCase(value))
				return LabelPosition.Top;
		}

		return defaultValue;
	}

	private static Direction getTimerDirection(StringMap properties, Direction defaultValue) {
		String value = properties.get(BuilderConstants.DIRECTION);

		if (value != null) {
			if ("up".equalsIgnoreCase(value)) {
				return Direction.UP;
			} else if ("down".equalsIgnoreCase(value)) {
				return Direction.DOWN;
			}
		}

		return defaultValue;
	}

	private static void setCaption(Component component, StringMap properties) {
		if (component instanceof AbstractComponent) {
			String caption = properties.get(BuilderConstants.CAPTION);
			if (caption != null) {
				component.setCaption(caption);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static void setCommonFieldProperties(AbstractField component, Element element, StringMap stringMap,
			AlignmentWrapper alignmentWrapper) {
		setCommonProperties(component, element, stringMap, alignmentWrapper);

		// set AbstractField specific properties
		component.setReadOnly(stringMap.getBoolean(BuilderConstants.READ_ONLY, false));
	}

	public static void setCommonLayoutProperties(AbstractOrderedLayout component, Element element, StringMap stringMap,
			AlignmentWrapper alignmentWrapper) {
		setCommonProperties(component, element, stringMap, alignmentWrapper);
		setLayoutSpacing(component, stringMap);
	}

	public static void setCommonProperties(Component component, Element element, StringMap stringMap,
			AlignmentWrapper alignmentWrapper) {
		// store component id
		if (component instanceof AbstractComponent)
			((AbstractComponent) component).setData(XmlDocumentUtility.getId(element));

		setCaption(component, stringMap);

		setWidth(component, stringMap.getDimension(BuilderConstants.WIDTH));
		setHeight(component, stringMap.getDimension(BuilderConstants.HEIGHT));
		component.setVisible(stringMap.getBoolean(BuilderConstants.VISIBLE, true));
		component.setEnabled(stringMap.getBoolean(BuilderConstants.ENABLED, true));

		setWrappedAlignment(stringMap, alignmentWrapper);
	}

	public static void setPanelProperties(Panel panel, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		setCommonProperties(panel, element, properties, alignmentWrapper);

		// set Panel specific properties
		// defaults to true
		boolean border = properties.getBoolean(BuilderConstants.BORDER, true);
		if (!border) {
			panel.addStyleName("borderless");
		}
	}

	public static void setComponentPanelProperties(MultipleComponentPanel<? extends AbstractComponent> component,
			Element element, StringMap stringMap, AlignmentWrapper alignmentWrapper) {
		setCommonProperties(component, element, stringMap, alignmentWrapper);

		Orientation orientation = getOrientation(stringMap, Orientation.Horizontal);
		component.setOrientation(orientation);

		setChildsSize(component, stringMap);
	}

	public static void setImageProperties(Image image, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		setCommonProperties(image, element, properties, alignmentWrapper);

		image.setSource(new ExternalResource(properties.get(BuilderConstants.URL, "")));
	}

	public static void setVideoProperties(Video video, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		setCommonProperties(video, element, properties, alignmentWrapper);

		setMediaSources(video, element);

		// TODO make localizable
		video.setAltText("Your browser doesn't support video element.");
	}

	public static void setAudioProperties(Audio audio, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		setCommonProperties(audio, element, properties, alignmentWrapper);

		setMediaSources(audio, element);

		audio.setAltText("Your browser doesn't support audio element.");
	}

	public static void setMediaSources(AbstractMedia media, Element component) {
		List<Element> elements = XmlDocumentUtility.getComponentSources(component);

		List<Resource> resources = new ArrayList<Resource>();
		for (Element element : elements) {
			String url = element.attributeValue(BuilderConstants.URL);
			if (!Strings.isNullOrEmpty(url)) {
				resources.add(new ExternalResource(url));
			}
		}

		if (!resources.isEmpty()) {
			media.setSources(resources.toArray(new Resource[0]));
		}
	}

	private static void setChildsSize(MultipleComponentPanel<? extends AbstractComponent> component,
			StringMap stringMap) {
		setChildsWidth(component, stringMap.getDimension(BuilderConstants.CHILD_WIDTH));
		setChildsHeight(component, stringMap.getDimension(BuilderConstants.CHILD_HEIGHT));
		if (component != null) {
			component.updateContent();
		}
	}

	private static void setChildsWidth(MultipleComponentPanel<? extends AbstractComponent> component,
			String dimension) {
		if (component != null) {
			component.setChildsWidth(dimension);
		}
	}

	private static void setChildsHeight(MultipleComponentPanel<? extends AbstractComponent> component,
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

	private static void setLayoutSpacing(AbstractOrderedLayout component, StringMap properties) {
		// TODO how to handle with spacing value?
		// int value = stringMap.getInteger(SlideXmlConstants.SPACING, -1);
		// if (value > 0)
		boolean value = properties.getBoolean(BuilderConstants.SPACING, false);
		component.setSpacing(value);
	}

	private static void setWrappedAlignment(StringMap properties, AlignmentWrapper alignmentWrapper) {
		if (alignmentWrapper != null) {
			String align = properties.get(BuilderConstants.ALIGNMENT);
			Alignment alignment = stringToAlignment(align);
			alignmentWrapper.setAlignment(alignment);
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

		// default
		return Alignment.MIDDLE_CENTER;
	}

	public static void setButtonProperties(Button component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		setCommonProperties(component, element, properties, alignmentWrapper);

		// set Button specific properties
		// TODO in future set dynamic css
	}

	public static void setButtonPanelProperties(ButtonPanel component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		component.setCaptions(properties.getStringArray(BuilderConstants.CAPTIONS));

		setComponentPanelProperties(component, element, properties, alignmentWrapper);
	}

	public static void setSelectPanelProperties(SelectPanel component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		component.setCaptions(properties.getStringArray(BuilderConstants.CAPTIONS));

		setComponentPanelProperties(component, element, properties, alignmentWrapper);

		// set SelectPanel specific properties
		component.setMultiSelect(properties.getBoolean(BuilderConstants.MULTI_SELECT, false));
		component.setLabelPosition(getLabelPosition(properties, LabelPosition.Right));
	}

	public static void setTextFieldProperties(TextField component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		setCommonFieldProperties(component, element, properties, alignmentWrapper);
	}

	public static void setTextAreaProperties(TextArea component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		setCommonFieldProperties(component, element, properties, alignmentWrapper);
	}

	public static void setDateFieldProperties(DateField component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		setCommonFieldProperties(component, element, properties, alignmentWrapper);
	}

	public static void setComboBoxProperties(ComboBox component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		setCommonFieldProperties(component, element, properties, alignmentWrapper);
	}

	public static void setTimerLabelProperties(TimerLabel component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		setLabelProperties(component, element, properties, alignmentWrapper);

		// TimerLabel specific properties
		component.setTimeFormat(properties.get(BuilderConstants.TIME_FORMAT, TimerLabel.DEAFAULT_TIME_FORMAT));
	}

	public static void setLabelProperties(Label component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		setCommonProperties(component, element, properties, alignmentWrapper);
	}

	public static void setTimerProperties(Timer component, Element element, StringMap properties) {
		component.setData(XmlDocumentUtility.getId(element));
		component.setTime(properties.getInteger(BuilderConstants.TIME, 0));
		component.setDirection(getTimerDirection(properties, Direction.UP));
	}

	public static void setWindowProperties(Window component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(component, element, properties, alignmentWrapper);

		// set Window specific properties
		// TODO in future set dynamic css
	}

}
