/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map;

import org.vaadin.maps.geometry.Utils;

import com.tilioteo.hypothesis.interfaces.CoreObject;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class GeomUtils implements CoreObject {

	public Geometry createFromText(String wkt) {
		try {
			return Utils.wktToGeometry(wkt);
		} catch (Throwable e) {
		}
		return null;
	}
	
}
