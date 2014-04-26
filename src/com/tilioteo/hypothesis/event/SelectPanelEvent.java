/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.ui.SelectPanel;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class SelectPanelEvent extends
		AbstractComponentEvent<SelectPanel> {

	public static class Click extends SelectPanelEvent {

		public Click(SelectPanelData data) {
			super(data);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.SelectPanelClick;
		}

	}

	protected SelectPanelEvent(SelectPanelData data) {
		super(data);
	}
}
