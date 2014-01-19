/**
 * 
 */
package org.hypothesis.application.collector.ui.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;
import org.hypothesis.application.collector.core.CommandFactory;
import org.hypothesis.application.collector.core.SlideFactory;
import org.hypothesis.application.collector.core.SlideManager;
import org.hypothesis.application.collector.core.SlideUtility;
import org.hypothesis.application.collector.events.Command;
import org.hypothesis.application.collector.events.RadioPanelData;
import org.hypothesis.application.collector.slide.AbstractBaseAction;
import org.hypothesis.application.collector.ui.component.RadioButton.LabelPosition;
import org.hypothesis.application.collector.xml.SlideXmlConstants;
import org.hypothesis.common.StringMap;
import org.hypothesis.common.Strings;

import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class RadioPanel extends MultipleComponentPanel<RadioButton> implements
		SlideComponent {

	private SlideManager slideManager;
	private ParentAlignment parentAlignment;

	private List<RadioButton.ClickListener> clickListeners = new ArrayList<RadioButton.ClickListener>();
	RadioButton selected = null;
	private String[] captions;

	public RadioPanel() {
		this.parentAlignment = new ParentAlignment();
	}

	public RadioPanel(SlideManager slideManager) {
		this();
		this.slideManager = slideManager;

		setStyleName("light");
		setSizeUndefined();
	}

	@Override
	protected void addChildsTo(List<RadioButton> list) {
		int i = 1;
		for (String caption : captions) {
			RadioButton radioButton = new RadioButton(caption);
			radioButton
					.setData(String.format("%s_%d",
							this.getData() != null ? (String) this.getData()
									: "", i++));

			for (RadioButton.ClickListener listener : clickListeners)
				radioButton.addListener(listener);

			list.add(radioButton);
		}
	}

	public void addRadioButtonClickListener(
			RadioButton.ClickListener radioButtonClickListener) {
		this.clickListeners.add(radioButtonClickListener);
	}

	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	private LabelPosition getLabelPosition(StringMap stringMap,
			LabelPosition defaultLabelPosition) {
		String labelPosition = stringMap.get(SlideXmlConstants.LABEL_POSITION);
		if (labelPosition != null) {
			if ("right".equals(labelPosition.toLowerCase()))
				return LabelPosition.Right;
			else if ("left".equals(labelPosition.toLowerCase()))
				return LabelPosition.Left;
			else if ("bottom".equals(labelPosition.toLowerCase()))
				return LabelPosition.Bottom;
			else if ("top".equals(labelPosition.toLowerCase()))
				return LabelPosition.Top;
		}
		return defaultLabelPosition;
	}

	public RadioButton getSelected() {
		return selected;
	}

	public void loadFromXml(Element element) {

		setProperties(element);
		setHandlers(element);

	}

	private void setClickHandler(String actionId) {
		final RadioPanelData data = new RadioPanelData(this, slideManager);
		final Command componentEvent = CommandFactory
				.createRadioPanelClickEventCommand(data);
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId);

		addRadioButtonClickListener(new RadioButton.ClickListener() {
			public void radioButtonClick(RadioButton.ClickEvent event) {
				data.setRadioButton((RadioButton) event.getSource());
				componentEvent.execute();
				action.execute();
			}
		});
	}

	protected void setHandler(Element element) {
		String name = element.getName();
		String action = null;
		AbstractBaseAction anonymousAction = SlideFactory.getInstatnce()
				.createAnonymousAction(element);
		if (anonymousAction != null)
			action = anonymousAction.getId();

		if (!Strings.isNullOrEmpty(action)) {
			if (name.equals(SlideXmlConstants.CLICK)) {
				setClickHandler(action);
			}
			// TODO add other event handlers
		}
	}

	private void setHandlers(Element element) {
		List<Element> handlers = SlideUtility.getHandlerElements(element);

		for (Element handler : handlers) {
			setHandler(handler);
		}
	}

	public void setLabelPosition(LabelPosition labelPosition) {
		Iterator<RadioButton> iterator = getChildIterator();
		while (iterator.hasNext()) {
			iterator.next().setLabelPosition(labelPosition);
		}
	}

	private void setLabelPosition(StringMap properties) {
		LabelPosition labelPosition = getLabelPosition(properties,
				LabelPosition.Right);
		setLabelPosition(labelPosition);
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		this.captions = properties.getStringArray(SlideXmlConstants.CAPTIONS);

		ComponentUtility.setComponentPanelProperties(this, element, properties,
				parentAlignment);

		// set RadioPanel specific properties
		setLabelPosition(properties);
	}

	public void setSelected(RadioButton radioButton) {
		this.selected = radioButton;
	}

	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

}
