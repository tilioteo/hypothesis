/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.ui.Button;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ButtonData extends AbstractComponentData<Button> {

	public static ButtonData cast(AbstractComponentData<Button> componentData) {
		return (ButtonData) componentData;
	}

	public ButtonData(Button sender, SlideManager slideManager) {
		super(sender, slideManager);
	}
}
