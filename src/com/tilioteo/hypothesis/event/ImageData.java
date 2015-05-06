/**
 * 
 */
package com.tilioteo.hypothesis.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.slide.ui.Image;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ImageData extends AbstractComponentData<Image> {

	// using 2D coordinates only
	private Coordinate coordinate;

	public ImageData(Image sender, SlideFascia slideFascia) {
		super(sender, slideFascia);
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
		SlideFactory.writeImageData(element, this);
	}
}
