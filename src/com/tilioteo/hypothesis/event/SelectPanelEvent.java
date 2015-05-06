/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.slide.ui.SelectPanel;
import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public abstract class SelectPanelEvent extends AbstractComponentEvent<SelectPanel> {

	public static class Click extends SelectPanelEvent {

		public Click(SelectPanelData data) {
			this(data, null);
		}

		public Click(SelectPanelData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.SelectPanelClick;
		}

	}

	protected SelectPanelEvent(SelectPanelData data, ErrorHandler errorHandler) {
		super(data, errorHandler);
	}
}
