/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import org.dom4j.Element;
import org.vaadin.maps.ui.feature.VectorFeature;

import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.plugin.map.SlideFactory;
import com.tilioteo.hypothesis.plugin.map.ui.DrawLineControl;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class DrawLineControlData extends AbstractComponentData<DrawLineControl> {

	private VectorFeature feature = null;

	public DrawLineControlData(DrawLineControl sender, SlideFascia slideFascia) {
		super(sender, slideFascia);
	}

	public VectorFeature getFeature() {
		return feature;
	}

	public void setFeature(VectorFeature feature) {
		this.feature = feature;
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeDrawLineControlData(element, this);
	}

}
