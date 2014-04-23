/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.ui;

import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;
import org.vaadin.maps.shared.ui.Style;
import org.vaadin.maps.ui.LayerLayout;
import org.vaadin.maps.ui.control.AbstractControl;
import org.vaadin.maps.ui.layer.ControlLayer;
import org.vaadin.maps.ui.layer.Layer;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.plugin.map.MapComponentFactory;
import com.tilioteo.hypothesis.plugin.map.MapUtility;
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
	
	private HashMap<String, Style> styles = new HashMap<String, Style>();
	
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
		addStyles(element);
		addLayers(element);
		addControls(element);
	}

	private void addStyles(Element element) {
		List<Element> elements = SlideXmlUtility.getStyles(element);
		for (Element childElement : elements) {
			String id = com.tilioteo.hypothesis.dom.SlideXmlUtility.getId(childElement);;
			if (id != null) {
				Style style = MapComponentFactory.createStyleFromElement(childElement);
				addStyle(id, style);
			}
		}
	}
	
	private void addStyle(String id, Style style) {
		if (id != null && style != null) {
			styles.put(id, style);
		}
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
		MapUtility.setMap(this);

		StringMap properties = SlideUtility.getPropertyValueMap(element);
		
		ComponentUtility.setCommonProperties(this, element, properties, parentAlignment);

		// set Map specific properties
	}

	public Style getStyle(String name) {
		return styles.get(name);
	}
}
