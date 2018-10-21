/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.hypothesis.common.utility.StringUtility.getBoolean;
import static org.hypothesis.common.utility.StringUtility.getDimension;
import static org.hypothesis.common.utility.StringUtility.getInteger;
import static org.hypothesis.common.utility.StringUtility.getStringArray;
import static org.hypothesis.interfaces.DocumentConstants.BORDER;
import static org.hypothesis.interfaces.DocumentConstants.DIRECTION;
import static org.hypothesis.interfaces.DocumentConstants.LABEL_POSITION;
import static org.hypothesis.interfaces.DocumentConstants.ORIENTATION;
import static org.vaadin.special.shared.ui.selectbutton.SelectButtonState.LabelPosition.Bottom;
import static org.vaadin.special.shared.ui.selectbutton.SelectButtonState.LabelPosition.Left;
import static org.vaadin.special.shared.ui.selectbutton.SelectButtonState.LabelPosition.Right;
import static org.vaadin.special.shared.ui.selectbutton.SelectButtonState.LabelPosition.Top;
import static org.vaadin.special.shared.ui.timer.TimerState.Direction.DOWN;
import static org.vaadin.special.shared.ui.timer.TimerState.Direction.UP;
import static org.vaadin.special.ui.MultipleComponentPanel.Orientation.Horizontal;
import static org.vaadin.special.ui.MultipleComponentPanel.Orientation.Vertical;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hypothesis.common.utility.ComponentUtility;
import org.hypothesis.common.utility.DocumentUtility;
import org.hypothesis.interfaces.AlignmentWrapper;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;
import org.hypothesis.slide.ui.Audio;
import org.hypothesis.slide.ui.Button;
import org.hypothesis.slide.ui.ButtonPanel;
import org.hypothesis.slide.ui.ClientSim;
import org.hypothesis.slide.ui.ComboBox;
import org.hypothesis.slide.ui.DateField;
import org.hypothesis.slide.ui.Image;
import org.hypothesis.slide.ui.Label;
import org.hypothesis.slide.ui.Panel;
import org.hypothesis.slide.ui.SelectPanel;
import org.hypothesis.slide.ui.TextArea;
import org.hypothesis.slide.ui.TextField;
import org.hypothesis.slide.ui.TimerLabel;
import org.hypothesis.slide.ui.Video;
import org.hypothesis.slide.ui.Window;
import org.vaadin.special.shared.ui.selectbutton.SelectButtonState.LabelPosition;
import org.vaadin.special.shared.ui.timer.TimerState.Direction;
import org.vaadin.special.ui.MultipleComponentPanel;
import org.vaadin.special.ui.MultipleComponentPanel.Orientation;
import org.vaadin.special.ui.Timer;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractMedia;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class SlideComponentUtility {

	private static Orientation getOrientation(Map<String, String> properties, Orientation defaultValue) {
		String value = properties.get(ORIENTATION);

		if (value != null) {
			if ("horizontal".equalsIgnoreCase(value))
				return Horizontal;
			else if ("vertical".equalsIgnoreCase(value))
				return Vertical;
		}

		return defaultValue;
	}

	private static LabelPosition getLabelPosition(Map<String, String> properties, LabelPosition defaultValue) {
		String value = properties.get(LABEL_POSITION);

		if (value != null) {
			if ("right".equalsIgnoreCase(value))
				return Right;
			else if ("left".equalsIgnoreCase(value))
				return Left;
			else if ("bottom".equalsIgnoreCase(value))
				return Bottom;
			else if ("top".equalsIgnoreCase(value))
				return Top;
		}

		return defaultValue;
	}

	private static Direction getTimerDirection(Map<String, String> properties, Direction defaultValue) {
		String value = properties.get(DIRECTION);

		if (value != null) {
			if ("up".equalsIgnoreCase(value)) {
				return UP;
			} else if ("down".equalsIgnoreCase(value)) {
				return DOWN;
			}
		}

		return defaultValue;
	}

	public static void setPanelProperties(Panel panel, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(panel, element, properties, alignmentWrapper);

		// set Panel specific properties
		// defaults to true
		boolean border = getBoolean(properties, BORDER, true);
		if (!border) {
			panel.addStyleName("borderless");
		}
	}

	public static void setComponentPanelProperties(MultipleComponentPanel<? extends AbstractComponent> component,
			Element element, Map<String, String> stringMap, AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(component, element, stringMap, alignmentWrapper);

		Orientation orientation = getOrientation(stringMap, Horizontal);
		component.setOrientation(orientation);

		setChildrenSize(component, stringMap);
		setChildrenStyle(component, stringMap);
	}

	public static void setImageProperties(Image image, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(image, element, properties, alignmentWrapper);

		image.setSource(new ExternalResource(properties.getOrDefault(DocumentConstants.URL, "")));
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
		List<Element> elements = DocumentUtility.getComponentSources(component);

		List<Resource> resources = new ArrayList<>();

		if (elements != null) {
			for (Element element : elements) {
				String url = element.getAttribute(DocumentConstants.URL);
				if (isNotEmpty(url)) {
					resources.add(new ExternalResource(url));
				}
			}
		}

		if (!resources.isEmpty()) {
			media.setSources(resources.toArray(new Resource[0]));
		}
	}

	private static void setChildrenSize(MultipleComponentPanel<? extends AbstractComponent> component,
			Map<String, String> stringMap) {
		setChildrenWidth(component, getDimension(stringMap, DocumentConstants.CHILD_WIDTH));
		setChildrenHeight(component, getDimension(stringMap, DocumentConstants.CHILD_HEIGHT));
		if (component != null) {
			component.updateContent();
		}
	}

	private static void setChildrenStyle(MultipleComponentPanel<? extends AbstractComponent> component,
			Map<String, String> stringMap) {
		String style = stringMap.get(DocumentConstants.CHILD_STYLE);
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
		component.setCaptions(getStringArray(properties, DocumentConstants.CAPTIONS));

		setComponentPanelProperties(component, element, properties, alignmentWrapper);
	}

	public static void setSelectPanelProperties(SelectPanel component, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		component.setCaptions(getStringArray(properties, DocumentConstants.CAPTIONS));

		setComponentPanelProperties(component, element, properties, alignmentWrapper);

		// set SelectPanel specific properties
		component.setMultiSelect(getBoolean(properties, DocumentConstants.MULTI_SELECT, false));
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
		component
				.setTimeFormat(properties.getOrDefault(DocumentConstants.TIME_FORMAT, TimerLabel.DEAFAULT_TIME_FORMAT));
	}

	public static void setLabelProperties(Label component, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(component, element, properties, alignmentWrapper);
	}

	public static void setTimerProperties(Timer component, Element element, Map<String, String> properties) {
		component.setData(DocumentUtility.getId(element));
		component.setTime(getInteger(properties, DocumentConstants.TIME, 0));
		component.setDirection(getTimerDirection(properties, Direction.UP));
	}

	public static void setWindowProperties(Window component, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		ComponentUtility.setCommonProperties(component, element, properties, alignmentWrapper);

		// set Window specific properties
		// TODO in future set dynamic css
	}

	public static void setClientSimProperties(ClientSim component, Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper) {
		setPanelProperties(component, element, properties, alignmentWrapper);
	}

}
