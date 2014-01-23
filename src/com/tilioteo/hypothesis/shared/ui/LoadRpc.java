/**
 * 
 */
package com.tilioteo.hypothesis.shared.ui;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 *
 */
public interface LoadRpc extends ServerRpc {
    /**
     * Called when a load event has occurred and there are server side
     * listeners for the event.
     * 
     */
	public void load();
	
    /**
     * Called when a load event has occurred and there are server side
     * listeners for the event.
     * 
     */
	public void error();
}
