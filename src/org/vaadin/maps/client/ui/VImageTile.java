/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.user.client.ui.Image;

/**
 * @author kamil
 *
 */
public class VImageTile extends Image {

    public static final String CLASSNAME = "v-imagetile";

    public VImageTile() {
        setStylePrimaryName(CLASSNAME);
        
        setAltText("");
    }
    
    @Override
    public void setUrl(String url) {
    	super.setUrl(url != null ? url : "");
    }
}
