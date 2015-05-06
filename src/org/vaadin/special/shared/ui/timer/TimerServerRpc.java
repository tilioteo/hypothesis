package org.vaadin.special.shared.ui.timer;

import com.vaadin.shared.communication.ServerRpc;

public interface TimerServerRpc extends ServerRpc {
	
	void started(long timestamp, long time, String direction, boolean resumed);
	void stopped(long timestamp, long time, String direction, boolean paused);
	//void update(long time, String direction, long interval);

}
