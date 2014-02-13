/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.plugin.map.ui.VectorFeature;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author kamil
 *
 */
public class VectorFeatureData extends AbstractComponentData<VectorFeature> {

	public static VectorFeatureData cast(AbstractComponentData<VectorFeature> componentData) {
		return (VectorFeatureData) componentData;
	}

	// using 2D coordinates only
	private Coordinate coordinate;

	public VectorFeatureData(VectorFeature sender, SlideManager slideManager) {
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
}
