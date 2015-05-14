/**
 * 
 */
package com.tilioteo.hypothesis.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.slide.ui.Button;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class ButtonData extends AbstractComponentData<Button> {

	public ButtonData(Button sender, SlideFascia slideFascia) {
		super(sender, slideFascia);
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeButtonData(element, this);		
	}
}
