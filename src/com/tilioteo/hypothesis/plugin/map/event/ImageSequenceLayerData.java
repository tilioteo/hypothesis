/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.plugin.map.SlideFactory;
import com.tilioteo.hypothesis.plugin.map.ui.ImageSequenceLayer;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author kamil
 *
 */
public class ImageSequenceLayerData extends AbstractComponentData<ImageSequenceLayer> {

	// using 2D coordinates only
	private Coordinate coordinate;
	
	private int imageIndex = 0;
	private String imageTag = null;

	public ImageSequenceLayerData(ImageSequenceLayer sender, SlideFascia slideFascia) {
		super(sender, slideFascia);
		// TODO Auto-generated constructor stub
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

	
	public int getImageIndex() {
		return imageIndex;
	}

	public void setImageIndex(int imageIndex) {
		this.imageIndex = imageIndex;
	}

	public String getImageTag() {
		return imageTag;
	}

	public void setImageTag(String imageTag) {
		this.imageTag = imageTag;
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeImageSequenceLayerData(element, this);
	}

}
