package com.tilioteo.hypothesis.shared.ui.timer;

import com.vaadin.shared.communication.ServerRpc;

public interface TimerServerRpc extends ServerRpc {
	
	void start(long time, String direction, boolean resumed);
	void stop(long time, String direction, boolean paused);
	//void update(long time, String direction, long timeSlice);

}
