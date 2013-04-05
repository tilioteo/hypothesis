/**
 * 
 */
package org.hypothesis.application.collector.events;

import org.hypothesis.application.collector.core.SlideManager;
import org.hypothesis.application.collector.ui.component.ButtonPanel;

import com.vaadin.ui.Button;

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
