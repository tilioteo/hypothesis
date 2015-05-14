/**
 * 
 */
package com.tilioteo.hypothesis.event;

import org.dom4j.Element;
import org.vaadin.special.ui.SelectButton;

import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.slide.ui.SelectPanel;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class SelectPanelData extends AbstractComponentData<SelectPanel> {

	private SelectButton button;

	public SelectPanelData(SelectPanel sender, SlideFascia slideFascia) {
		super(sender, slideFascia);
	}

	public SelectButton getButton() {
		return button;
	}

	public int getButtonIndex() {
		if (button != null) {
			return getSender().getChildIndex(button) + 1;
		} else {
			return 0;
		}
	}

	public void setButton(SelectButton button) {
		this.button = button;
	}
	
	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeSelectPanelData(element, this);
	}
}
