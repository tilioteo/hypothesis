/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.ui.Button;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class ButtonEvent extends AbstractComponentEvent<Button> {

	public static class Click extends ButtonEvent {

		public Click(ButtonData data) {
			super(data);
		}

		public String getName() {
			return ProcessEventTypes.ButtonClick;
		}
	}

	protected ButtonEvent(ButtonData data) {
		super(data);
	}
}
