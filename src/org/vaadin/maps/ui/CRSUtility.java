/**
 * 
 */
package org.vaadin.maps.ui;

import org.osgeo.proj4j.CRSFactory;

/**
 * @author kamil
 *
 */
public class CRSUtility {
	
	public static boolean checkCRS(String crsString) {
		CRSFactory crsFactory = new CRSFactory();
		try {
			crsFactory.createFromName(crsString);
			return true;
		} catch (Exception e) {
			
		}
		
		return false;
	}

}
