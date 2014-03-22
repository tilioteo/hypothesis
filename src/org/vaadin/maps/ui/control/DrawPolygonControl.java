/**
 * 
 */
package org.vaadin.maps.ui.control;

import org.vaadin.maps.ui.handler.PolygonHandler;
import org.vaadin.maps.ui.layer.VectorFeatureLayer;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class DrawPolygonControl extends DrawFeatureControl<PolygonHandler> {

	public DrawPolygonControl(VectorFeatureLayer layer) {
		super(layer);
	}

}
