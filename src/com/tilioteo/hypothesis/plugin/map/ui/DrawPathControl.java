/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.ui;

import java.util.List;

import org.dom4j.Element;
import org.vaadin.maps.ui.handler.FeatureHandler;
import org.vaadin.maps.ui.handler.FeatureHandler.GeometryEvent;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.plugin.map.MapComponentFactory;
import com.tilioteo.hypothesis.plugin.map.MapUtility;
import com.tilioteo.hypothesis.plugin.map.SlideXmlConstants;
import com.tilioteo.hypothesis.plugin.map.event.DrawPathControlData;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.tilioteo.hypothesis.ui.SlideComponent;
import com.vaadin.ui.Alignment;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class DrawPathControl extends org.vaadin.maps.ui.control.DrawPathControl implements SlideComponent {

	private SlideManager slideManager;
	
	public DrawPathControl() {
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
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		MapUtility.setDrawFeatureControlProperties(this, element, properties, slideManager);
		
		// set DrawPathControl specific properties
		MapUtility.setDrawPathControlProperties(this, element, properties, slideManager);
	}

	private void setHandlers(Element element) {
		List<Element> handlers = SlideUtility.getHandlerElements(element);

		for (Element handler : handlers) {
			setHandler(handler);
		}
	}

	protected void setHandler(Element element) {
		String name = element.getName();
		String action = null;
		AbstractBaseAction anonymousAction = SlideFactory.getInstatnce()
				.createAnonymousAction(element);
		if (anonymousAction != null)
			action = anonymousAction.getId();

		if (!Strings.isNullOrEmpty(action)) {
			if (name.equals(SlideXmlConstants.DRAW)) {
				setDrawHandler(action);
			}
			// TODO add other event handlers
		}
	}

	private void setDrawHandler(String actionId) {
		final DrawPathControlData data = new DrawPathControlData(this, slideManager);
		final Command componentEvent = MapComponentFactory.createDrawPathControlEventCommand(data);
		final Command action = CommandFactory.createActionCommand(slideManager,	actionId, data);

		addGeomertyListener(new FeatureHandler.GeometryListener() {
			@Override
			public void geometry(GeometryEvent event) {
				data.setGeometry(event.getGeometry());
				componentEvent.execute();
				action.execute();
			}
		});
	}


	@Override
	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

}
