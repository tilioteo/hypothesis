/**
 * 
 */
package org.hypothesis.application.collector.events;

import org.hypothesis.application.collector.ui.component.ButtonPanel;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class ButtonPanelEvent extends
		AbstractComponentEvent<ButtonPanel> {

	public static class Click extends ButtonPanelEvent {

		public Click(/* ProcessEvent event, */ButtonPanelData data) {
			super(/* event, */data);
		}

		public String getName() {
			return ProcessEvents.ButtonPanelClick;
		}
	}

	protected ButtonPanelEvent(/* ProcessEvent event, */ButtonPanelData data) {
		super(/* event, */data);
	}
}
