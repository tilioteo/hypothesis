/**
 * 
 */
package com.tilioteo.hypothesis.shared.ui.video;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 *
 */
public interface VideoServerRpc extends ServerRpc {

    /**
     * Called when a click event has occurred and there are server side
     * listeners for the event.
     * 
     * @param mouseDetails
     *            Details about the mouse when the event took place
     */
    public void click(MouseEventDetails mouseDetails, double time);
    
	void start(double time, boolean resumed);
	
	void stop(double time, boolean paused);
	
	void canPlayThrough();
}
