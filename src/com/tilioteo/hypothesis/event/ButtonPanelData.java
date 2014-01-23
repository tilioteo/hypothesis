/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.ui.Button;
import com.tilioteo.hypothesis.ui.ButtonPanel;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ButtonPanelData extends AbstractComponentData<ButtonPanel> {

	public static ButtonPanelData cast(
			AbstractComponentData<ButtonPanel> componentData) {
		return (ButtonPanelData) componentData;
	}

	private Button button;

	public ButtonPanelData(ButtonPanel sender, SlideManager slideManager) {
		super(sender, slideManager);
	}

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
	}
}
