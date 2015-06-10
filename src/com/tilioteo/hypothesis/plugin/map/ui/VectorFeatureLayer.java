/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.ui;

import java.util.List;

import org.dom4j.Element;
import org.vaadin.maps.server.LonLat;
import org.vaadin.maps.ui.featurecontainer.VectorFeatureContainer.ClickEvent;
import org.vaadin.maps.ui.featurecontainer.VectorFeatureContainer.ClickListener;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.plugin.map.MapComponentFactory;
import com.tilioteo.hypothesis.plugin.map.MapUtility;
import com.tilioteo.hypothesis.plugin.map.SlideXmlConstants;
import com.tilioteo.hypothesis.plugin.map.SlideXmlUtility;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureLayerData;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.vaadin.ui.Alignment;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class VectorFeatureLayer extends org.vaadin.maps.ui.layer.VectorFeatureLayer implements SlideComponent {
	
	private SlideFascia slideFascia;
	
	public VectorFeatureLayer() {
		super();
	}

	@Override
	public Alignment getAlignment() {
		return null;
	}

	@Override
	public void loadFromXml(Element element) {
		setProperties(element);
		setHandlers(element);
		addFeatures(element);
	}

	private void addFeatures(Element element) {
		List<Element> elements = SlideXmlUtility.getFeatures(element, SlideXmlConstants.VALID_FEATURE_ELEMENTS);
		for (Element childElement : elements) {
			SlideComponent component = MapComponentFactory.createComponentFromElement(childElement, slideFascia);
			if (component instanceof VectorFeature) {
				addComponent((VectorFeature)component);
			}
		}
	}

	@Override
	public void setSlideManager(SlideFascia slideFascia) {
		this.slideFascia = slideFascia;
	}

	protected void setProperties(Element element) {
		StringMap properties = com.tilioteo.hypothesis.dom.SlideXmlUtility.getPropertyValueMap(element);
		MapUtility utility = MapUtility.getInstance(slideFascia);
		if (utility != null) {
			utility.setLayerProperties(this, element, properties);

			// set VectorFeatureLayer specific properties
			utility.setFeatureLayerProperties(this, element, properties);
		}
	}

	private void setHandlers(Element element) {
		List<Element> handlers = com.tilioteo.hypothesis.dom.SlideXmlUtility.getComponentHandlers(element);

		for (Element handler : handlers) {
			setHandler(handler);
		}
	}

	protected void setHandler(Element element) {
		String name = element.getName();
		String action = null;
		AbstractBaseAction anonymousAction = SlideFactory.getInstance(slideFascia).createAnonymousAction(element);
		if (anonymousAction != null)
			action = anonymousAction.getId();

		if (!Strings.isNullOrEmpty(action)) {
			if (name.equals(SlideXmlConstants.CLICK)) {
				setClickHandler(action);
			}
			// TODO add other event handlers
		}
	}

	private void setClickHandler(final String actionId) {
		addClickListener(new ClickListener() {
			@Override
			public void click(ClickEvent event) {
				VectorFeatureLayerData data = new VectorFeatureLayerData(VectorFeatureLayer.this, slideFascia);
				data.setXY(event.getRelativeX(), event.getRelativeY());
				if (getForLayer() != null) {
					LonLat lonLat = getForLayer().getViewWorldTransform().viewToWorld(event.getRelativeX(), event.getRelativeY());
					if (lonLat != null) {
						data.setWorldXY(lonLat.getLon(), lonLat.getLat());
					}
				}

				Command componentEvent = MapComponentFactory.createVectorFeatureLayerClickEventCommand(data,
						event.getServerDatetime(), event.getClientDatetime());
				Command action = CommandFactory.createActionCommand(slideFascia, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}
	
	/**
	 * Create and add feature to the layer
	 * @param geometry
	 * @return created feature or null
	 */
	public VectorFeature createFeature(Geometry geometry) {
		if (geometry != null) {
			VectorFeature feature = new VectorFeature();
			feature.setSlideManager(slideFascia);
			feature.setGeometry(geometry);
			
			addComponent(feature);
			return feature;
		}
		
		return null;
	}

}
