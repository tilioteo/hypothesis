/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder.impl;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractMedia;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hypothesis.common.utility.ComponentUtility;
import org.hypothesis.common.utility.ConversionUtility;
import org.hypothesis.common.utility.DocumentUtility;
import org.hypothesis.interfaces.AlignmentWrapper;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;
import org.hypothesis.slide.ui.*;
import org.vaadin.special.shared.ui.selectbutton.SelectButtonState.LabelPosition;
import org.vaadin.special.shared.ui.timer.TimerState.Direction;
import org.vaadin.special.ui.MultipleComponentPanel;
import org.vaadin.special.ui.MultipleComponentPanel.Orientation;
import org.vaadin.special.ui.Timer;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public final class SlideComponentUtility {

	private SlideComponentUtility() {
	}

	private static Orientation getOrientation(Map<String, String> properties, Orientation defaultValue) {
		String value = properties.get(DocumentConstants.ORIENTATION);

		if (value != null) {
			if ("horizontal".equalsIgnoreCase(value))
				return Orientation.Horizontal;
			else if ("vertical".equalsIgnoreCase(value))
				return Orientation.Vertical;
		}

		return defaultValue;
	}

	private static LabelPosition getLabelPosition(Map<String, String> properties, LabelPosition defaultValue) {
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

	private static Direction getTimerDirection(Map<String, String> properties, Direction defaultValue) {
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

	public static void setPanelProperties(Panel panel, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(panel, element, properties, alignmentWrapper);

		// set Panel specific properties
		// defaults to true
		boolean border = ConversionUtility.getBooleanOrDefault(properties.get(DocumentConstants.BORDER), true);
		if (!border) {
			panel.addStyleName("borderless");
		}
	}

	public static void setComponentPanelProperties(MultipleComponentPanel<? extends AbstractComponent> component,
			Element element, Map<String, String> stringMap, AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(component, element, stringMap, alignmentWrapper);

		Orientation orientation = getOrientation(stringMap, Orientation.Horizontal);
		component.setOrientation(orientation);

		setChildrenSize(component, stringMap);
		setChildrenStyle(component, stringMap);
	}

	public static void setImageProperties(Image image, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(image, element, properties, alignmentWrapper);

		image.setSource(
				new ExternalResource(ConversionUtility.getStringOrDefault(properties.get(DocumentConstants.URL), "")));
	}

	public static void setVideoProperties(Video video, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(video, element, properties, alignmentWrapper);

		setMediaSources(video, element);

		// TODO make localizable
		video.setAltText("Your browser doesn't support video element.");
	}

	public static void setAudioProperties(Audio audio, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(audio, element, properties, alignmentWrapper);

		setMediaSources(audio, element);

		audio.setAltText("Your browser doesn't support audio element.");
	}

	public static void setMediaSources(AbstractMedia media, Element component) {
		Resource[] resources = DocumentUtility.getComponentSources(component).stream()
				.map(m -> m.getAttribute(DocumentConstants.URL)).filter(StringUtils::isNotEmpty)
				.map(ExternalResource::new).toArray(s -> new Resource[s]);

		if (ArrayUtils.isNotEmpty(resources)) {
			media.setSources(resources);
		}
	}

	private static void setChildrenSize(MultipleComponentPanel<? extends AbstractComponent> component,
			Map<String, String> properties) {
		setChildrenWidth(component, ConversionUtility.getDimension(properties.get(DocumentConstants.CHILD_WIDTH)));
		setChildrenHeight(component, ConversionUtility.getDimension(properties.get(DocumentConstants.CHILD_HEIGHT)));
		if (component != null) {
			component.updateContent();
		}
	}

	private static void setChildrenStyle(MultipleComponentPanel<? extends AbstractComponent> component,
			Map<String, String> properties) {
		String style = properties.get(DocumentConstants.CHILD_STYLE);
		if (component != null && style != null) {
			component.setChildrenStyle(style);
			component.updateContent();
		}
	}

	private static void setChildrenWidth(MultipleComponentPanel<? extends AbstractComponent> component,
			String dimension) {
		if (component != null) {
			component.setChildrenWidth(dimension);
		}
	}

	private static void setChildrenHeight(MultipleComponentPanel<? extends AbstractComponent> component,
			String dimension) {
		if (component != null) {
			component.setChildrenHeight(dimension);
		}
	}

	public static void setButtonProperties(Button component, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(component, element, properties, alignmentWrapper);

		// set Button specific properties
		// TODO in future set dynamic css
	}

	public static void setButtonPanelProperties(ButtonPanel component, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		component.setCaptions(Arrays
				.stream(properties.getOrDefault(DocumentConstants.CAPTIONS, "")
						.split(DocumentConstants.STR_QUOTED_STRING_SPLIT_PATTERN))
				.map(m -> StringUtils.strip(m, DocumentConstants.STR_QUOTE)).toArray(s -> new String[s]));

		setComponentPanelProperties(component, element, properties, alignmentWrapper);
	}

	public static void setSelectPanelProperties(SelectPanel component, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {

		component.setCaptions(Arrays
				.stream(properties.getOrDefault(DocumentConstants.CAPTIONS, "")
						.split(DocumentConstants.STR_QUOTED_STRING_SPLIT_PATTERN))
				.map(m -> StringUtils.strip(m, DocumentConstants.STR_QUOTE)).toArray(s -> new String[s]));

		setComponentPanelProperties(component, element, properties, alignmentWrapper);

		// set SelectPanel specific properties
		component.setMultiSelect(
				ConversionUtility.getBooleanOrDefault(properties.get(DocumentConstants.MULTI_SELECT), false));
		component.setLabelPosition(getLabelPosition(properties, LabelPosition.Right));
	}

	public static void setTextFieldProperties(TextField component, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonFieldProperties(component, element, properties, alignmentWrapper);
	}

	public static void setTextAreaProperties(TextArea component, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonFieldProperties(component, element, properties, alignmentWrapper);
	}

	public static void setDateFieldProperties(DateField component, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonFieldProperties(component, element, properties, alignmentWrapper);
	}

	public static void setComboBoxProperties(ComboBox component, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonFieldProperties(component, element, properties, alignmentWrapper);
	}

	public static void setTimerLabelProperties(TimerLabel component, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		setLabelProperties(component, element, properties, alignmentWrapper);

		// TimerLabel specific properties
		component.setTimeFormat(ConversionUtility.getStringOrDefault(properties.get(DocumentConstants.TIME_FORMAT),
				TimerLabel.DEAFAULT_TIME_FORMAT));
	}

	public static void setLabelProperties(Label component, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(component, element, properties, alignmentWrapper);
	}

	public static void setTimerProperties(Timer component, Element element, Map<String, String> properties) {
		DocumentUtility.getId(element).ifPresent(component::setData);
		component.setTime(ConversionUtility.getIntegerOrDefault(properties.get(DocumentConstants.TIME), 0));
		component.setDirection(getTimerDirection(properties, Direction.UP));
	}

	public static void setWindowProperties(Window component, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(component, element, properties, alignmentWrapper);

		// set Window specific properties
		// TODO in future set dynamic css
	}

}
