/**
 * 
 */
package org.hypothesis.application.collector.events;

import org.hypothesis.application.collector.core.SlideManager;
import org.hypothesis.application.collector.ui.component.RadioButton;
import org.hypothesis.application.collector.ui.component.RadioPanel;

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
