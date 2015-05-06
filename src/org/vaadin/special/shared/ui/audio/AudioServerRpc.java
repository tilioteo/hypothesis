/**
 * 
 */
package org.vaadin.special.shared.ui.audio;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 *
 */
public interface AudioServerRpc extends ServerRpc {

	void start(long timestamp, double time, boolean resumed);
	
	void stop(long timestamp, double time, boolean paused);
	
	void canPlayThrough(long timestamp);

}
