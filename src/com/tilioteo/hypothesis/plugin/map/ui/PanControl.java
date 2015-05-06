/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.ui;

import java.util.List;

import org.dom4j.Element;
import org.vaadin.maps.server.LonLat;
import org.vaadin.maps.ui.handler.PanHandler;
import org.vaadin.maps.ui.handler.PanHandler.PanEndEvent;
import org.vaadin.maps.ui.handler.PanHandler.PanStartEvent;
import org.vaadin.maps.ui.layer.ForLayer;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.plugin.map.MapComponentFactory;
import com.tilioteo.hypothesis.plugin.map.MapUtility;
import com.tilioteo.hypothesis.plugin.map.SlideXmlConstants;
import com.tilioteo.hypothesis.plugin.map.event.PanControlData;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.vaadin.ui.Alignment;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PanControl extends org.vaadin.maps.ui.control.PanControl implements SlideComponent {

	private SlideFascia slideFascia;
	
	public PanControl() {
		super(null);
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

	protected void setProperties(Element element) {
		StringMap properties = SlideXmlUtility.getPropertyValueMap(element);
		MapUtility utility = MapUtility.getInstance(slideFascia);
		if (utility != null) {
			utility.setNavigateControlProperties(this, element, properties);
		
			// set PanControl specific properties
		}
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
			if (name.equals(SlideXmlConstants.START)) {
				setPanStartHandler(action);
			} else if (name.equals(SlideXmlConstants.END)) {
				setPanEndHandler(action);
			}
			// TODO add other event handlers
		}
	}

	private void setPanStartHandler(final String actionId) {
		addPanStartListener(new PanHandler.PanStartListener() {
			@Override
			public void panStart(PanStartEvent event) {
				PanControlData data = new PanControlData(PanControl.this, slideFascia);
				data.setXY(event.getX(), event.getY());
				if (getLayout() != null && getLayout() instanceof ForLayer) {
					ForLayer forLayer = (ForLayer)getLayout();
					LonLat lonLat = forLayer.getViewWorldTransform().viewToWorld(event.getX(), event.getY());
					if (lonLat != null) {
						data.setWorldXY(lonLat.getLon(), lonLat.getLat());
					}
				}

				Command componentEvent = MapComponentFactory.createPanControlPanStartEventCommand(data,
						event.getServerDatetime(), event.getClientDatetime());
				Command action = CommandFactory.createActionCommand(slideFascia, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	private void setPanEndHandler(final String actionId) {
		addPanEndListener(new PanHandler.PanEndListener() {
			@Override
			public void panEnd(PanEndEvent event) {
				PanControlData data = new PanControlData(PanControl.this, slideFascia);
				data.setDeltaXY(event.getDeltaX(), event.getDeltaY());
				if (getLayout() != null && getLayout() instanceof ForLayer) {
					ForLayer forLayer = (ForLayer)getLayout();
					LonLat topLeft = forLayer.getViewWorldTransform().viewToWorld(0, 0);
					LonLat lonLat = forLayer.getViewWorldTransform().viewToWorld(event.getDeltaX(), event.getDeltaY());
					if (topLeft != null && lonLat != null) {
						data.setWorldDeltaXY(
								lonLat.getLon() - topLeft.getLon(),
								topLeft.getLat() - lonLat.getLat());
					}
				}

				Command componentEvent = MapComponentFactory.createPanControlPanEndEventCommand(data,
						event.getServerDatetime(), event.getClientDatetime());
				Command action = CommandFactory.createActionCommand(slideFascia, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	@Override
	public void setSlideManager(SlideFascia slideFascia) {
		this.slideFascia = slideFascia;
	}

}
