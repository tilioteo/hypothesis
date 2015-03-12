/**
 * 
 */
package com.tilioteo.hypothesis.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.ui.Video;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author kamil
 *
 */
public class VideoData extends AbstractComponentData<Video> {

	private Coordinate coordinate;
	private double time = 0.0;

	public VideoData(Video sender, SlideManager slideManager) {
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
	
	public final double getTime() {
		return time;
	}
	
	public final void setTime(double time) {
		this.time = time;
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeVideoData(element, this);
	}

}
