/**
 * 
 */
package org.vaadin.maps.server;

import java.io.Serializable;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class WMSResource implements TileResource, Serializable {

	/* (non-Javadoc)
	 * @see com.vaadin.server.Resource#getMIMEType()
	 */
	@Override
	public String getMIMEType() {
		return null;
	}

}
