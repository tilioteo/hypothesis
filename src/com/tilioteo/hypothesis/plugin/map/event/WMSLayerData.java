/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.plugin.map.SlideFactory;
import com.tilioteo.hypothesis.plugin.map.ui.WMSLayer;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author kamil
 *
 */
public class WMSLayerData extends AbstractComponentData<WMSLayer> {

	// using 2D coordinates only
	private Coordinate coordinate;
	private Coordinate worldCoordinate;

	public WMSLayerData(WMSLayer sender, SlideFascia slideFascia) {
		super(sender, slideFascia);
	}

	public final Coordinate getCoordinate() {
		return coordinate;
	}

	public final Coordinate getWorldCoordinate() {
		return worldCoordinate;
	}

	public final boolean hasCoordinate() {
		return coordinate != null;
	}

	public final boolean hasWorldCoordinate() {
		return worldCoordinate != null;
	}

	public final void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public final void setWorldCoordinate(Coordinate coordinate) {
		this.worldCoordinate = coordinate;
	}

	public final void setXY(double x, double y) {
		if (coordinate != null) {
			coordinate.x = x;
			coordinate.y = y;
		} else
			coordinate = new Coordinate(x, y);
	}

	public final void setWorldXY(double x, double y) {
		if (worldCoordinate != null) {
			worldCoordinate.x = x;
			worldCoordinate.y = y;
		} else
			worldCoordinate = new Coordinate(x, y);
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeWMSLayerData(element, this);
	}
}
