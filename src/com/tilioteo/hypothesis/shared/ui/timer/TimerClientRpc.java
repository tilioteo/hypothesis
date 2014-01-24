package com.tilioteo.hypothesis.shared.ui.timer;

import com.vaadin.shared.communication.ClientRpc;

public interface TimerClientRpc extends ClientRpc {

	public void start(long time);
	public void stop();
	public void pause();
	public void resume();
	public boolean isRunning();
	
}