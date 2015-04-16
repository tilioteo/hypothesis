/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.ui;

import java.util.List;

import org.dom4j.Element;
import org.vaadin.maps.server.Bounds;
import org.vaadin.maps.shared.ui.Style;
import org.vaadin.maps.ui.HasLayerLayout;
import org.vaadin.maps.ui.MapContainer;
import org.vaadin.maps.ui.control.AbstractControl;
import org.vaadin.maps.ui.layer.AbstractLayer;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.plugin.map.MapComponentFactory;
import com.tilioteo.hypothesis.plugin.map.MapUtility;
import com.tilioteo.hypothesis.plugin.map.SlideXmlConstants;
import com.tilioteo.hypothesis.plugin.map.SlideXmlUtility;
import com.tilioteo.hypothesis.ui.ComponentUtility;
import com.tilioteo.hypothesis.ui.Mask;
import com.tilioteo.hypothesis.ui.Maskable;
import com.tilioteo.hypothesis.ui.ParentAlignment;
import com.tilioteo.hypothesis.ui.SlideComponent;
import com.vaadin.ui.Alignment;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class Map extends MapContainer implements SlideComponent, Maskable {

	private SlideManager slideManager;
	private ParentAlignment parentAlignment;
	private Mask mask = null;
	
	public Map() {
		this.parentAlignment = new ParentAlignment();
	}
	
	public Map(SlideManager slideManager) {
		this();
		setSlideManager(slideManager);
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
			String id = SlideXmlUtility.getId(childElement);;
			if (id != null) {
				Style style = MapComponentFactory.createStyleFromElement(childElement);
				addStyle(id, style);
			}
		}
	}
	
	private void addLayers(Element element) {
		List<Element> elements = SlideXmlUtility.getLayers(element, SlideXmlConstants.VALID_LAYER_ELEMENTS);
		for (Element childElement : elements) {
			SlideComponent component = MapComponentFactory.createComponentFromElement(childElement, slideManager);
			if (component instanceof AbstractLayer<?>) {
				addLayer((AbstractLayer<?>)component);
			}
		}
	}

	private void addControls(Element element) {
		List<Element> elements = SlideXmlUtility.getControls(element, SlideXmlConstants.VALID_CONTROL_ELEMENTS);
		for (Element childElement : elements) {
			SlideComponent component = MapComponentFactory.createComponentFromElement(childElement, slideManager);
			if (component instanceof AbstractControl) {
				if (component instanceof HasLayerLayout) {
					((HasLayerLayout)component).setLayout(this);
				}
				addControl((AbstractControl)component);
			}
		}
	}

	@Override
	public void setSlideManager(SlideManager slideManager) {
		if (this.slideManager != slideManager) {
			MapUtility.remove(this.slideManager);
			this.slideManager = slideManager;
			MapUtility.newInstance(slideManager, this);
		}
	}

	protected void setProperties(Element element) {
		StringMap properties = com.tilioteo.hypothesis.dom.SlideXmlUtility.getPropertyValueMap(element);
		
		ComponentUtility.setCommonProperties(this, element, properties, parentAlignment);

		// set Map specific properties
		setCRS(properties.get(SlideXmlConstants.CRS));
		setBounds(Bounds.fromBBOX(properties.get(SlideXmlConstants.BBOX)));
	}

	@Override
	public void mask() {
		if (null == mask) {
			mask = Mask.addToComponent(this);
		}
		mask.show();
	}

	@Override
	public void unmask() {
		if (mask != null) {
			mask.hide();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		MapUtility.remove(slideManager);
		
		super.finalize();
	}

}
