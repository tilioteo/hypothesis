/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.plugin.map.SlideFactory;
import com.tilioteo.hypothesis.plugin.map.ui.PanControl;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PanControlData extends AbstractComponentData<PanControl> {

	// using 2D coordinates only
	private Coordinate coordinate;
	private Coordinate worldCoordinate;

	private Coordinate delta;
	private Coordinate worldDelta;

	public PanControlData(PanControl sender, SlideFascia slideFascia) {
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

	public final Coordinate getDelta() {
		return delta;
	}

	public final Coordinate getWorldDelta() {
		return worldDelta;
	}

	public final boolean hasDelta() {
		return delta != null;
	}

	public final boolean hasWorldDelta() {
		return worldDelta != null;
	}

	public final void setDelta(Coordinate delta) {
		this.delta = delta;
	}

	public final void setWorldDelta(Coordinate delta) {
		this.worldDelta = delta;
	}

	public final void setDeltaXY(double deltaX, double deltaY) {
		if (delta != null) {
			delta.x = deltaX;
			delta.y = deltaY;
		} else
			delta = new Coordinate(deltaX, deltaY);
	}

	public final void setWorldDeltaXY(double deltaX, double deltaY) {
		if (worldDelta != null) {
			worldDelta.x = deltaX;
			worldDelta.y = deltaY;
		} else
			worldDelta = new Coordinate(deltaX, deltaY);
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writePanControlData(element, this);
	}

}
