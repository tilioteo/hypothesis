/**
 * 
 */
package org.vaadin.special.shared.ui.video;

import org.vaadin.special.shared.ui.audio.AudioServerRpc;

import com.vaadin.shared.MouseEventDetails;

/**
 * @author kamil
 *
 */
public interface VideoServerRpc extends AudioServerRpc {

    /**
     * Called when a click event has occurred and there are server side
     * listeners for the event.
     * 
     * @param mouseDetails
     *            Details about the mouse when the event took place
     */
    public void click(long timestamp, MouseEventDetails mouseDetails, double mediaTime);
    
}
