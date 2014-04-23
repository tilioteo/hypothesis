/**
 * 
 */
package com.tilioteo.hypothesis.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.ui.RadioButton;
import com.tilioteo.hypothesis.ui.RadioPanel;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class RadioPanelData extends AbstractComponentData<RadioPanel> {

	private RadioButton radioButton;

	public RadioPanelData(RadioPanel sender, SlideManager slideManager) {
		super(sender, slideManager);
	}

	public RadioButton getRadioButton() {
		return radioButton;
	}

	public int getRadioButtonIndex() {
		if (radioButton != null) {
			return getSender().getChildIndex(radioButton) + 1;
		} else {
			return 0;
		}
	}

	public void setRadioButton(RadioButton radioButton) {
		this.radioButton = radioButton;
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeRadioPanelData(element, this);
	}
}
