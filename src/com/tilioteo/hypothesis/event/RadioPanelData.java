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

	public void setRadioButton(RadioButton radioButton) {
		this.radioButton = radioButton;
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeRadioPanelData(element, this);
	}
}
