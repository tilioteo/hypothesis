/**
 * 
 */
package com.tilioteo.hypothesis.event;

import org.dom4j.Element;
import org.vaadin.special.ui.Button;
import org.vaadin.special.ui.ButtonPanel;

import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.interfaces.SlideFascia;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ButtonPanelData extends AbstractComponentData<ButtonPanel> {

	private Button button;

	public ButtonPanelData(ButtonPanel sender, SlideFascia slideFascia) {
		super(sender, slideFascia);
	}

	public Button getButton() {
		return button;
	}
	
	public int getButtonIndex() {
		if (button != null) {
			return getSender().getChildIndex(button) + 1;
		} else {
			return 0;
		}
	}

	public void setButton(Button button) {
		this.button = button;
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeButtonPanelData(element, this);
	}
}
