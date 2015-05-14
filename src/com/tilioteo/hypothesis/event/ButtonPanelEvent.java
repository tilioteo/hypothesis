/**
 * 
 */
package com.tilioteo.hypothesis.event;

import org.vaadin.special.ui.ButtonPanel;

import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class ButtonPanelEvent extends AbstractComponentEvent<ButtonPanel> {

	public static class Click extends ButtonPanelEvent {

		public Click(ButtonPanelData data) {
			this(data, null);
		}

		public Click(ButtonPanelData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.ButtonPanelClick;
		}
	}

	protected ButtonPanelEvent(ButtonPanelData data, ErrorHandler errorHandler) {
		super(data, errorHandler);
	}
}
