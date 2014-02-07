/**
 * 
 */
package org.vaadin.maps.ui.control;

import org.vaadin.maps.ui.handler.PathHandler;
import org.vaadin.maps.ui.layer.VectorFeatureLayer;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class DrawPathControl extends DrawFeatureControl<PathHandler> {

	public DrawPathControl(VectorFeatureLayer layer) {
		super(layer);
	}

}
