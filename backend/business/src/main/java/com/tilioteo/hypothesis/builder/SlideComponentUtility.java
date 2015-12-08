/**
 * 
 */
package com.tilioteo.hypothesis.builder;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.special.shared.ui.selectbutton.SelectButtonState.LabelPosition;
import org.vaadin.special.shared.ui.timer.TimerState.Direction;
import org.vaadin.special.ui.MultipleComponentPanel;
import org.vaadin.special.ui.MultipleComponentPanel.Orientation;

import com.tilioteo.common.Strings;
import com.tilioteo.common.collections.StringMap;
import com.tilioteo.hypothesis.common.utility.ComponentUtility;
import com.tilioteo.hypothesis.common.utility.DocumentUtility;
import com.tilioteo.hypothesis.interfaces.AlignmentWrapper;
import com.tilioteo.hypothesis.interfaces.DocumentConstants;
import com.tilioteo.hypothesis.interfaces.Element;
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
import com.vaadin.ui.AbstractMedia;

/**
 * @author kamil
 *
 */
public class SlideComponentUtility {

	private static Orientation getOrientation(StringMap properties, Orientation defaultValue) {
		String value = properties.get(DocumentConstants.ORIENTATION);

		if (value != null) {
			if ("horizontal".equalsIgnoreCase(value))
				return Orientation.Horizontal;
			else if ("vertical".equalsIgnoreCase(value))
				return Orientation.Vertical;
		}

		return defaultValue;
	}

	private static LabelPosition getLabelPosition(StringMap properties, LabelPosition defaultValue) {
		String value = properties.get(DocumentConstants.LABEL_POSITION);

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
		String value = properties.get(DocumentConstants.DIRECTION);

		if (value != null) {
			if ("up".equalsIgnoreCase(value)) {
				return Direction.UP;
			} else if ("down".equalsIgnoreCase(value)) {
				return Direction.DOWN;
			}
		}

		return defaultValue;
	}

	public static void setPanelProperties(Panel panel, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(panel, element, properties, alignmentWrapper);

		// set Panel specific properties
		// defaults to true
		boolean border = properties.getBoolean(DocumentConstants.BORDER, true);
		if (!border) {
			panel.addStyleName("borderless");
		}
	}

	public static void setComponentPanelProperties(MultipleComponentPanel<? extends AbstractComponent> component,
			Element element, StringMap stringMap, AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(component, element, stringMap, alignmentWrapper);

		Orientation orientation = getOrientation(stringMap, Orientation.Horizontal);
		component.setOrientation(orientation);

		setChildsSize(component, stringMap);
	}

	public static void setImageProperties(Image image, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(image, element, properties, alignmentWrapper);

		image.setSource(new ExternalResource(properties.get(DocumentConstants.URL, "")));
	}

	public static void setVideoProperties(Video video, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(video, element, properties, alignmentWrapper);

		setMediaSources(video, element);

		// TODO make localizable
		video.setAltText("Your browser doesn't support video element.");
	}

	public static void setAudioProperties(Audio audio, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(audio, element, properties, alignmentWrapper);

		setMediaSources(audio, element);

		audio.setAltText("Your browser doesn't support audio element.");
	}

	public static void setMediaSources(AbstractMedia media, Element component) {
		List<Element> elements = DocumentUtility.getComponentSources(component);

		List<Resource> resources = new ArrayList<Resource>();
		for (Element element : elements) {
			String url = element.getAttribute(DocumentConstants.URL);
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
		setChildsWidth(component, stringMap.getDimension(DocumentConstants.CHILD_WIDTH));
		setChildsHeight(component, stringMap.getDimension(DocumentConstants.CHILD_HEIGHT));
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

	public static void setButtonProperties(Button component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(component, element, properties, alignmentWrapper);

		// set Button specific properties
		// TODO in future set dynamic css
	}

	public static void setButtonPanelProperties(ButtonPanel component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		component.setCaptions(properties.getStringArray(DocumentConstants.CAPTIONS));

		setComponentPanelProperties(component, element, properties, alignmentWrapper);
	}

	public static void setSelectPanelProperties(SelectPanel component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		component.setCaptions(properties.getStringArray(DocumentConstants.CAPTIONS));

		setComponentPanelProperties(component, element, properties, alignmentWrapper);

		// set SelectPanel specific properties
		component.setMultiSelect(properties.getBoolean(DocumentConstants.MULTI_SELECT, false));
		component.setLabelPosition(getLabelPosition(properties, LabelPosition.Right));
	}

	public static void setTextFieldProperties(TextField component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonFieldProperties(component, element, properties, alignmentWrapper);
	}

	public static void setTextAreaProperties(TextArea component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonFieldProperties(component, element, properties, alignmentWrapper);
	}

	public static void setDateFieldProperties(DateField component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonFieldProperties(component, element, properties, alignmentWrapper);
	}

	public static void setComboBoxProperties(ComboBox component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonFieldProperties(component, element, properties, alignmentWrapper);
	}

	public static void setTimerLabelProperties(TimerLabel component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		setLabelProperties(component, element, properties, alignmentWrapper);

		// TimerLabel specific properties
		component.setTimeFormat(properties.get(DocumentConstants.TIME_FORMAT, TimerLabel.DEAFAULT_TIME_FORMAT));
	}

	public static void setLabelProperties(Label component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(component, element, properties, alignmentWrapper);
	}

	public static void setTimerProperties(Timer component, Element element, StringMap properties) {
		component.setData(DocumentUtility.getId(element));
		component.setTime(properties.getInteger(DocumentConstants.TIME, 0));
		component.setDirection(getTimerDirection(properties, Direction.UP));
	}

	public static void setWindowProperties(Window component, Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(component, element, properties, alignmentWrapper);

		// set Window specific properties
		// TODO in future set dynamic css
	}

}
