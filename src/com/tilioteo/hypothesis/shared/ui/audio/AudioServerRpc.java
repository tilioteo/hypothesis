/**
 * 
 */
package com.tilioteo.hypothesis.shared.ui.audio;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 *
 */
public interface AudioServerRpc extends ServerRpc {

	void start(double time, boolean resumed);
	
	void stop(double time, boolean paused);
	
	void canPlayThrough();

}
