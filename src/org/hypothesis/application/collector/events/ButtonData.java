/**
 * 
 */
package org.hypothesis.application.collector.events;

import org.hypothesis.application.collector.core.SlideManager;

import com.vaadin.ui.Button;

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
