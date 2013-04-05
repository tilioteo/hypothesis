/**
 * 
 */
package org.hypothesis.application.collector.events;

import com.vaadin.ui.Button;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class ButtonEvent extends AbstractComponentEvent<Button> {

	public static class Click extends ButtonEvent {

		public Click(/* ProcessEvent event, */ButtonData data) {
			super(/* event, */data);
		}

		public String getName() {
			return ProcessEvents.ButtonClick;
		}
	}

	protected ButtonEvent(/* ProcessEvent event, */ButtonData data) {
		super(/* event, */data);
	}
}
