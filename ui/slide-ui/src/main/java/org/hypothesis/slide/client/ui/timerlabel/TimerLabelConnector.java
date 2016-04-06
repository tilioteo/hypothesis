/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.slide.client.ui.timerlabel;

import org.hypothesis.slide.client.ui.VTimerLabel;
import org.hypothesis.slide.shared.ui.timerlabel.TimerLabelState;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Connect(org.hypothesis.slide.ui.TimerLabel.class)
public class TimerLabelConnector extends AbstractComponentConnector {

	@Override
	protected void init() {
		super.init();

	}

	@Override
	public VTimerLabel getWidget() {
		return (VTimerLabel) super.getWidget();
	}

	@Override
	public TimerLabelState getState() {
		return (TimerLabelState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

		if (stateChangeEvent.hasPropertyChanged("timeFormat")) {
			getWidget().setTimeFormat(getState().timeFormat);
		}

		if (stateChangeEvent.hasPropertyChanged("updateInterval")) {
			getWidget().setUpdateInterval(getState().updateInterval);
		}

		if (stateChangeEvent.hasPropertyChanged("timer")) {
			getWidget().registerTimer(getState().timer);
		}
	}

}
