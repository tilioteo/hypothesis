/**
 * 
 */
package org.vaadin.maps.shared.ui.feature;

import org.vaadin.maps.shared.ui.DoubleClickRpc;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.ClickRpc;

/**
 * @author kamil
 *
 */
public interface FeatureServerRpc extends ClickRpc, DoubleClickRpc {

    /**
     * Called when a double click event has occurred and there are server side
     * listeners for the event.
     * 
     * @param mouseDetails
     *            Details about the mouse when the event took place
     */
    public void doubleClick(MouseEventDetails mouseDetails);

}
