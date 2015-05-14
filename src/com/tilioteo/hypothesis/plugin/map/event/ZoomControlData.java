/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.plugin.map.SlideFactory;
import com.tilioteo.hypothesis.plugin.map.ui.ZoomControl;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ZoomControlData extends AbstractComponentData<ZoomControl> {

	private double zoomStep;
	
	public ZoomControlData(ZoomControl sender, SlideFascia slideFascia) {
		super(sender, slideFascia);
	}
	
	public double getZoomStep() {
		return zoomStep;
	}
	
	public void setZoomStep(double zoomStep) {
		this.zoomStep = zoomStep;
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeZoomControlData(element, this);
	}

}
