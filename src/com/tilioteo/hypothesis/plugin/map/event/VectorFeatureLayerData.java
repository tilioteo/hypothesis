/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.plugin.map.SlideFactory;
import com.tilioteo.hypothesis.plugin.map.ui.VectorFeatureLayer;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author kamil
 *
 */
public class VectorFeatureLayerData extends AbstractComponentData<VectorFeatureLayer> {

	// using 2D coordinates only
	private Coordinate coordinate;

	public VectorFeatureLayerData(VectorFeatureLayer sender, SlideManager slideManager) {
		super(sender, slideManager);
	}

	public final Coordinate getCoordinate() {
		return coordinate;
	}

	public final boolean hasCoordinate() {
		return coordinate != null;
	}

	public final void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public final void setXY(double x, double y) {
		if (coordinate != null) {
			coordinate.x = x;
			coordinate.y = y;
		} else
			coordinate = new Coordinate(x, y);
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeVectorFeatureLayerData(element, this);
	}
}
