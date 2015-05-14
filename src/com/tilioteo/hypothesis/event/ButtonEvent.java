/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.slide.ui.Button;
import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class ButtonEvent extends AbstractComponentEvent<Button> {

	public static class Click extends ButtonEvent {

		public Click(ButtonData data) {
			this(data, null);
		}

		public Click(ButtonData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.ButtonClick;
		}
	}

	protected ButtonEvent(ButtonData data, ErrorHandler errorHandler) {
		super(data, errorHandler);
	}
}
