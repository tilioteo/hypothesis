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

import com.tilioteo.common.collections.StringMap;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.plugin.map.MapComponentFactory;
import com.tilioteo.hypothesis.plugin.map.MapUtility;
import com.tilioteo.hypothesis.plugin.map.SlideXmlConstants;
import com.tilioteo.hypothesis.plugin.map.SlideXmlUtility;
import com.tilioteo.hypothesis.slide.ui.ComponentUtility;
import com.tilioteo.hypothesis.slide.ui.Mask;
import com.tilioteo.hypothesis.slide.ui.Maskable;
import com.tilioteo.hypothesis.slide.ui.ParentAlignment;
import com.vaadin.ui.Alignment;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class Map extends MapContainer implements SlideComponent, Maskable {

	private SlideFascia slideFascia;
	private ParentAlignment parentAlignment;
	private Mask mask = null;
	
	public Map() {
		this.parentAlignment = new ParentAlignment();
	}
	
	public Map(SlideFascia slideManager) {
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
			SlideComponent component = MapComponentFactory.createComponentFromElement(childElement, slideFascia);
			if (component instanceof AbstractLayer<?>) {
				addLayer((AbstractLayer<?>)component);
			}
		}
	}

	private void addControls(Element element) {
		List<Element> elements = SlideXmlUtility.getControls(element, SlideXmlConstants.VALID_CONTROL_ELEMENTS);
		for (Element childElement : elements) {
			SlideComponent component = MapComponentFactory.createComponentFromElement(childElement, slideFascia);
			if (component instanceof AbstractControl) {
				if (component instanceof HasLayerLayout) {
					((HasLayerLayout)component).setLayout(this);
				}
				addControl((AbstractControl)component);
			}
		}
	}

	@Override
	public void setSlideManager(SlideFascia slideFascia) {
		if (this.slideFascia != slideFascia) {
			MapUtility.remove(this.slideFascia);
			this.slideFascia = slideFascia;
			MapUtility.newInstance(slideFascia, this);
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
		mask.setColor("#808080");
		mask.show();
	}

	@Override
	public void mask(String color) {
		if (null == mask) {
			mask = Mask.addToComponent(this);
		}
		mask.setColor(color);
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
		MapUtility.remove(slideFascia);
		
		super.finalize();
	}

}
