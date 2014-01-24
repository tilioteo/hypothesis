package com.tilioteo.hypothesis.client.ui.timer;

import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.tilioteo.hypothesis.client.Timer;
import com.tilioteo.hypothesis.client.Timer.StartEvent;
import com.tilioteo.hypothesis.client.Timer.StopEvent;
import com.tilioteo.hypothesis.client.ui.VTimer;
import com.tilioteo.hypothesis.shared.ui.timer.TimerClientRpc;
import com.tilioteo.hypothesis.shared.ui.timer.TimerServerRpc;
import com.tilioteo.hypothesis.shared.ui.timer.TimerState;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;

@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.ui.Timer.class)
public class TimerConnector extends AbstractComponentConnector implements Timer.StartEventHandler, Timer.StopEventHandler {

    @Override
    protected void init() {
        super.init();
        getWidget().addStartEventHandler(this);
        getWidget().addStopEventHandler(this);
        
        registerRpc(TimerClientRpc.class, new TimerClientRpc() {
			
			@Override
			public void start(long time) {
				getWidget().start(time);
			}

			@Override
			public void stop() {
				getWidget().stop();
			}

			@Override
			public void pause() {
				getWidget().pause();
			}

			@Override
			public void resume() {
				getWidget().resume();
			}

			@Override
			public boolean isRunning() {
				return getWidget().isRunning();
			}

		});
        
        addStateChangeHandler("direction", new StateChangeHandler() {
			@Override
			public void onStateChanged(StateChangeEvent stateChangeEvent) {
				getWidget().setDirection(getState().direction.name());
				
			}
		});
    }

	@Override
	public VTimer getWidget() {
		return (VTimer) super.getWidget();
	}

	@Override
	public TimerState getState() {
		return (TimerState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

		// TODO do something useful
		//final String text = getState().text;
		//getWidget().setText(text);
	}

	@Override
	public void start(StartEvent event) {
		getRpcProxy(TimerServerRpc.class).start(event.getTime(), event.getDirection().name(), event.isResumed());
	}

	@Override
	public void stop(StopEvent event) {
		getRpcProxy(TimerServerRpc.class).stop(event.getTime(), event.getDirection().name(), event.isPaused());
	}

}

