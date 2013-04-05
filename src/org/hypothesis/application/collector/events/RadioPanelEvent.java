/**
 * 
 */
package org.hypothesis.application.collector.events;

import org.hypothesis.application.collector.ui.component.RadioPanel;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class RadioPanelEvent extends
		AbstractComponentEvent<RadioPanel> {

	public static class Click extends RadioPanelEvent {

		public Click(/* ProcessEvent event, */RadioPanelData data) {
			super(/* event, */data);
		}

		public String getName() {
			return ProcessEvents.RadioPanelClick;
		}

	}

	protected RadioPanelEvent(/* ProcessEvent event, */RadioPanelData data) {
		super(/* event, */data);
	}
}
