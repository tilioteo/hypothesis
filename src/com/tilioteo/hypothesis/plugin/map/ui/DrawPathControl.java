/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.ui;

import java.util.List;

import org.dom4j.Element;
import org.vaadin.maps.ui.handler.FeatureHandler;
import org.vaadin.maps.ui.handler.FeatureHandler.DrawFeatureEvent;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
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
		StringMap properties = SlideXmlUtility.getPropertyValueMap(element);
		MapUtility utility = MapUtility.getInstance(slideManager);
		if (utility != null) {
			utility.setDrawFeatureControlProperties(this, element, properties, slideManager);
		
			// set DrawPathControl specific properties
			utility.setDrawPathControlProperties(this, element, properties, slideManager);
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
		AbstractBaseAction anonymousAction = SlideFactory.getInstance(slideManager).createAnonymousAction(element);
		if (anonymousAction != null)
			action = anonymousAction.getId();

		if (!Strings.isNullOrEmpty(action)) {
			if (name.equals(SlideXmlConstants.DRAW)) {
				setDrawHandler(action);
			}
			// TODO add other event handlers
		}
	}

	private void setDrawHandler(final String actionId) {
		addDrawFeatureListener(new FeatureHandler.DrawFeatureListener() {
			@Override
			public void drawFeature(DrawFeatureEvent event) {
				DrawPathControlData data = new DrawPathControlData(DrawPathControl.this, slideManager);
				data.setFeature(event.getFeature());

				Command componentEvent = MapComponentFactory.createDrawPathControlEventCommand(data);
				Command action = CommandFactory.createActionCommand(slideManager, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}


	@Override
	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

}
