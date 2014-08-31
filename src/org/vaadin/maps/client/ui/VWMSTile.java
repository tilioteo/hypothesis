/**
 * 
 */
package org.vaadin.maps.client.ui;

/**
 * @author kamil
 *
 */
public class VWMSTile extends VImageTile {

    public static final String CLASSNAME = "v-wmstile";

    public VWMSTile() {
        setStylePrimaryName(CLASSNAME);
    }

    @Override
    public void setUrl(String url) {
    	
    	// TODO check wms request
    	super.setUrl(url);
    }
}
