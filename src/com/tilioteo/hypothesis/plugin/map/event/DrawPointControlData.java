/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.plugin.map.SlideFactory;
import com.tilioteo.hypothesis.plugin.map.ui.DrawPointControl;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author kamil
 *
 */
public class DrawPointControlData extends AbstractComponentData<DrawPointControl> {
	
	private Geometry geometry = null;

	public DrawPointControlData(DrawPointControl sender, SlideManager slideManager) {
		super(sender, slideManager);
	}
	
	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeDrawPointControlData(element, this);
	}

}
