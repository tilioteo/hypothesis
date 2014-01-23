/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.ui.RadioPanel;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class RadioPanelEvent extends
		AbstractComponentEvent<RadioPanel> {

	public static class Click extends RadioPanelEvent {

		public Click(RadioPanelData data) {
			super(data);
		}

		public String getName() {
			return ProcessEventTypes.RadioPanelClick;
		}

	}

	protected RadioPanelEvent(RadioPanelData data) {
		super(data);
	}
}
