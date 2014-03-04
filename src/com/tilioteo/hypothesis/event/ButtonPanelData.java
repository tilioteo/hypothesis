/**
 * 
 */
package com.tilioteo.hypothesis.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.ui.Button;
import com.tilioteo.hypothesis.ui.ButtonPanel;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ButtonPanelData extends AbstractComponentData<ButtonPanel> {

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

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeButtonPanelData(element, this);
	}
}
