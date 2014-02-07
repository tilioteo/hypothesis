/**
 * 
 */
package org.vaadin.maps.client.ui;

import org.vaadin.gwtgraphics.client.AbstractDrawing;
import org.vaadin.gwtgraphics.client.AbstractDrawingContainer;
import org.vaadin.gwtgraphics.client.Group;
import org.vaadin.maps.client.geometry.Geometry;
import org.vaadin.maps.client.geometry.Utils;

/**
 * @author kamil
 *
 */
public class VVectorFeature extends AbstractDrawingContainer {
	
	public static final String CLASSNAME = "v-vectorfeature";
	
	private Geometry geometry = null;
	private AbstractDrawing drawing = null;
	
	public VVectorFeature() {
		super();
		setStyleName(CLASSNAME);
	}
	
	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		if (geometry != null) {
			if (!geometry.equals(this.geometry)) {
				clear();
				// create new vector object and insert it into feature root element
				drawGeometry(geometry);
			}
		} else {
			clear();
		}

		this.geometry = geometry;
	}
	
	@Override
	public void clear() {
		super.clear();
		drawing = null;
	}

	private void drawGeometry(Geometry geometry) {
		drawing = Utils.drawGeometry(geometry);
		add(drawing);
	}
	
	public AbstractDrawing getDrawing() {
		return drawing;
	}

	/**
	 * Returns type of Group class, constructor will create its implementation as root element 
	 */
	@Override
	protected Class<? extends AbstractDrawing> getType() {
		return Group.class;
	}

}
