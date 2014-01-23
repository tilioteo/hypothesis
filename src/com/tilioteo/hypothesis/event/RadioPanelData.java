/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.ui.RadioButton;
import com.tilioteo.hypothesis.ui.RadioPanel;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class RadioPanelData extends AbstractComponentData<RadioPanel> {

	public static RadioPanelData cast(
			AbstractComponentData<RadioPanel> componentData) {
		return (RadioPanelData) componentData;
	}

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
}
