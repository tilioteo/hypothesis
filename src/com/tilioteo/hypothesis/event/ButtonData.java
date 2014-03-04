/**
 * 
 */
package com.tilioteo.hypothesis.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.ui.Button;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ButtonData extends AbstractComponentData<Button> {

	public ButtonData(Button sender, SlideManager slideManager) {
		super(sender, slideManager);
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeButtonData(element, this);		
	}
}
