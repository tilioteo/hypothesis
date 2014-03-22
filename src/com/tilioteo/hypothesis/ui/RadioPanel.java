/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.Field;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.event.RadioPanelData;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.tilioteo.hypothesis.shared.ui.radiobutton.RadioButtonState.LabelPosition;
import com.tilioteo.hypothesis.ui.RadioButton.ClickEvent;
import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class RadioPanel extends MultipleComponentPanel<RadioButton> implements
		SlideComponent, Field {

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

	protected void addChilds() {
		int i = 1;
		for (String caption : captions) {
			RadioButton radioButton = new RadioButton(caption);
			radioButton
					.setData(String.format("%s_%d",
							this.getData() != null ? (String) this.getData()
									: "", i++));

			for (RadioButton.ClickListener listener : clickListeners)
				radioButton.addClickListener(listener);

			addChild(radioButton);
		}
		updateContent();
	}
	
	public void addRadioButtonClickListener(
			RadioButton.ClickListener radioButtonClickListener) {
		this.clickListeners.add(radioButtonClickListener);
	}

	@Override
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

	@Override
	public void loadFromXml(Element element) {

		setProperties(element);
		setHandlers(element);

		addChilds();
	}

	private void setClickHandler(String actionId) {
		final RadioPanelData data = new RadioPanelData(this, slideManager);
		final Command componentEvent = CommandFactory
				.createRadioPanelClickEventCommand(data);
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId);

		addRadioButtonClickListener(new RadioButton.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
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

	@Override
	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

	@Override
	public void readDataFromElement(Element element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeDataToElement(Element element) {
		element.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.RADIO_PANEL);
		element.addAttribute(SlideXmlConstants.ID, (String) getData());
		Element captionElement = element.addElement(SlideXmlConstants.CAPTION);
		if (getCaption() != null) {
			captionElement.addText(getCaption());
		}
		Element valueElement = element.addElement(SlideXmlConstants.VALUE);
		if (selected != null) {
			valueElement.addAttribute(SlideXmlConstants.ID, String.format("%d", getChildIndex(selected)));
			valueElement.addText(selected.getCaption());
		}
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
