/**
 * 
 */
package org.vaadin.maps.client.ui.tile;

import org.vaadin.maps.client.ui.VWMSTile;
import org.vaadin.maps.shared.ui.tile.WMSTileState;

import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.tile.WMSTile.class)
public class WMSTileConnector extends ImageTileConnector {
    
    @Override
    public VWMSTile getWidget() {
        return (VWMSTile) super.getWidget();
    }

    @Override
    public WMSTileState getState() {
        return (WMSTileState) super.getState();
    }

}
