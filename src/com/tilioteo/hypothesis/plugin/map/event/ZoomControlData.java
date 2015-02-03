/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.plugin.map.SlideFactory;
import com.tilioteo.hypothesis.plugin.map.ui.ZoomControl;

/**
 * @author kamil
 *
 */
public class ZoomControlData extends AbstractComponentData<ZoomControl> {

	private double zoomStep;
	
	public ZoomControlData(ZoomControl sender, SlideManager slideManager) {
		super(sender, slideManager);
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
