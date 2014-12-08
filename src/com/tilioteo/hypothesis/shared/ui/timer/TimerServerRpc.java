package com.tilioteo.hypothesis.shared.ui.timer;

import com.vaadin.shared.communication.ServerRpc;

public interface TimerServerRpc extends ServerRpc {
	
	void started(long time, String direction, boolean resumed);
	void stopped(long time, String direction, boolean paused);
	//void update(long time, String direction, long interval);

}
