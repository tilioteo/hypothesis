package com.tilioteo.hypothesis.client.ui.timer;

import java.util.Set;

import com.vaadin.shared.ui.Connect;
import com.tilioteo.hypothesis.client.Timer;
import com.tilioteo.hypothesis.client.Timer.StartEvent;
import com.tilioteo.hypothesis.client.Timer.StopEvent;
import com.tilioteo.hypothesis.client.Timer.UpdateEvent;
import com.tilioteo.hypothesis.client.ui.AbstractNonVisualComponentConnector;
import com.tilioteo.hypothesis.client.ui.VTimer;
import com.tilioteo.hypothesis.shared.ui.timer.TimerClientRpc;
import com.tilioteo.hypothesis.shared.ui.timer.TimerServerRpc;
import com.tilioteo.hypothesis.shared.ui.timer.TimerState;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;

@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.ui.Timer.class)
public class TimerConnector extends AbstractNonVisualComponentConnector implements
		Timer.StartEventHandler, Timer.StopEventHandler,
		Timer.UpdateEventHandler {

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
			public void stop(boolean silent) {
				getWidget().stop(silent);
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
			public void getRunning() {
				getState().running = getWidget().isRunning();
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

		if (stateChangeEvent.hasPropertyChanged("intervals")) {
			Set<Long> requiredIntervals = getState().intervals;
			Set<Long> handledIntervals = getWidget().getHandledIntervals();

			// unregister unwanted interval handlers
			for (Long interval : handledIntervals) {
				if (!requiredIntervals.contains(interval)) {
					getWidget().removeUpdateEventHandler(interval, this);
				}
			}
			// register required interval handlers
			for (Long interval : requiredIntervals) {
				if (!getWidget().hasUpdateEventHandler(interval, this)) {
					getWidget().addUpdateEventHandler(interval, this);
				}
			}
		}
	}

	@Override
	public void start(final StartEvent event) {
		getRpcProxy(TimerServerRpc.class).started(event.getTime(), event.getDirection().name(), event.isResumed());
	}
	
	@Override
	public void stop(final StopEvent event) {
		getRpcProxy(TimerServerRpc.class).stopped(event.getTime(), event.getDirection().name(), event.isPaused());
	}

	@Override
	public void update(final UpdateEvent event) {
		//getRpcProxy(TimerServerRpc.class).update(event.getTime(), event.getDirection().name(), event.getInterval());
	}

}
