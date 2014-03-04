/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.ui;

import java.util.List;

import org.dom4j.Element;
import org.vaadin.maps.ui.LayerLayout;
import org.vaadin.maps.ui.control.AbstractControl;
import org.vaadin.maps.ui.layer.ControlLayer;
import org.vaadin.maps.ui.layer.Layer;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.plugin.map.MapComponentFactory;
import com.tilioteo.hypothesis.plugin.map.SlideXmlConstants;
import com.tilioteo.hypothesis.plugin.map.SlideXmlUtility;
import com.tilioteo.hypothesis.ui.ComponentUtility;
import com.tilioteo.hypothesis.ui.ParentAlignment;
import com.tilioteo.hypothesis.ui.SlideComponent;
import com.vaadin.ui.Alignment;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class Map extends LayerLayout implements SlideComponent {

	private SlideManager slideManager;
	private ParentAlignment parentAlignment;
	
	private ControlLayer controlLayer;
	
	public Map() {
		this.parentAlignment = new ParentAlignment();
	}
	
	public Map(SlideManager slideManager) {
		this();
		this.slideManager = slideManager;
	}

	@Override
	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	@Override
	public void loadFromXml(Element element) {
		setProperties(element);
		addLayers(element);
		addControls(element);
	}

	private void addLayers(Element element) {
		// Add default control layer
		controlLayer = new ControlLayer();
		addComponent(controlLayer);
		
		List<Element> elements = SlideXmlUtility.getLayers(
				element, SlideXmlConstants.VALID_LAYER_ELEMENTS);
		for (Element childElement : elements) {
			SlideComponent component = MapComponentFactory.createComponentFromElement(childElement, slideManager);
			if (component instanceof Layer) {
				addComponent((Layer)component);
			}
		}
	}

	private void addControls(Element element) {
		List<Element> elements = SlideXmlUtility.getControls(
				element, SlideXmlConstants.VALID_CONTROL_ELEMENTS);
		for (Element childElement : elements) {
			SlideComponent component = MapComponentFactory.createComponentFromElement(childElement, slideManager);
			if (component instanceof AbstractControl) {
				controlLayer.addComponent((AbstractControl)component);
			}
		}
	}

	@Override
	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);
		
		ComponentUtility.setCommonProperties(this, element, properties, parentAlignment);

		// set Map specific properties
		
	}

}
