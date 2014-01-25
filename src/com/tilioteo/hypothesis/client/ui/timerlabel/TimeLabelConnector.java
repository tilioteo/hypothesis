/**
 * 
 */
package com.tilioteo.hypothesis.client.ui.timerlabel;

import com.tilioteo.hypothesis.client.ui.VTimer;
import com.tilioteo.hypothesis.client.ui.VTimerLabel;
import com.tilioteo.hypothesis.shared.ui.timerlabel.TimerLabelState;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.ui.TimerLabel.class)
public class TimeLabelConnector extends AbstractComponentConnector {

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
		
		if (stateChangeEvent.hasPropertyChanged("timer")) {
			getWidget().registerTimer(getState().timer);
		}
	}

}
