/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.ui.ButtonPanel;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class ButtonPanelEvent extends
		AbstractComponentEvent<ButtonPanel> {

	public static class Click extends ButtonPanelEvent {

		public Click(ButtonPanelData data) {
			super(data);
		}

		public String getName() {
			return ProcessEventTypes.ButtonPanelClick;
		}
	}

	protected ButtonPanelEvent(ButtonPanelData data) {
		super(data);
	}
}
