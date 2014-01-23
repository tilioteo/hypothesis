/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.ui.Image;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ImageData extends AbstractComponentData<Image> {

	public static ImageData cast(AbstractComponentData<Image> componentData) {
		return (ImageData) componentData;
	}

	// using 2D coordinates only
	private Coordinate coordinate;

	public ImageData(Image sender, SlideManager slideManager) {
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
