/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.ui;

import java.util.List;

import org.dom4j.Element;
import org.vaadin.maps.event.MouseEvents.ClickEvent;
import org.vaadin.maps.event.MouseEvents.ClickListener;
import org.vaadin.maps.server.LonLat;
import org.vaadin.maps.server.WMSConstants;

import com.tilioteo.common.Strings;
import com.tilioteo.common.collections.StringMap;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.plugin.map.MapComponentFactory;
import com.tilioteo.hypothesis.plugin.map.MapUtility;
import com.tilioteo.hypothesis.plugin.map.SlideXmlConstants;
import com.tilioteo.hypothesis.plugin.map.event.WMSLayerData;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.vaadin.ui.Alignment;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class WMSLayer extends org.vaadin.maps.ui.layer.WMSLayer implements SlideComponent {
	private SlideFascia slideFascia;
	
	public WMSLayer() {
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
	}

	@Override
	public void setSlideManager(SlideFascia slideFascia) {
		this.slideFascia = slideFascia;
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideXmlUtility.getPropertyValueMap(element);
		MapUtility utility = MapUtility.getInstance(slideFascia);
		if (utility != null) {
			utility.setLayerProperties(this, element, properties);
		}

		// set WMSLayer specific properties
		setBaseUrl(properties.get(SlideXmlConstants.URL, ""));
		setFormat(properties.get(SlideXmlConstants.FORMAT, WMSConstants.DEFAULT_FORMAT));
		setLayers(properties.get(SlideXmlConstants.LAYERS, ""));
		setStyles(properties.get(SlideXmlConstants.STYLES, ""));
	}

	private void setHandlers(Element element) {
		List<Element> handlers = SlideXmlUtility.getComponentHandlers(element);

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
			}/* else if (name.equals(SlideXmlConstants.LOAD)) {
				setLoadHandler(action);
			}*/
			// TODO add other event handlers
		}
	}

	private void setClickHandler(final String actionId) {
		addClickListener(new ClickListener() {
			@Override
			public void click(ClickEvent event) {
				WMSLayerData data = new WMSLayerData(WMSLayer.this, slideFascia);
				data.setXY(event.getRelativeX(), event.getRelativeY());
				if (getForLayer() != null) {
					LonLat lonLat = getForLayer().getViewWorldTransform().viewToWorld(event.getRelativeX(), event.getRelativeY());
					if (lonLat != null) {
						data.setWorldXY(lonLat.getLon(), lonLat.getLat());
					}
				}

				Command componentEvent = MapComponentFactory.createWMSLayerClickEventCommand(data,
						event.getServerDatetime(), event.getClientDatetime());
				Command action = CommandFactory.createActionCommand(slideFascia, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	/*private void setLoadHandler(final String actionId) {
		addLoadListener(new LoadListener() {
			@Override
			public void load(LoadEvent event) {
				WMSLayerData data = new WMSLayerData(ImageLayer.this, slideFascia);
				Command componentEvent = MapComponentFactory.createWMSLayerLoadEventCommand(data);
				Command action = CommandFactory.createActionCommand(slideFascia, actionId, data);
				
				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}*/

}
