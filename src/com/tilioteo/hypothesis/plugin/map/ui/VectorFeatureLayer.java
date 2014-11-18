/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.ui;

import java.util.List;

import org.dom4j.Element;
import org.vaadin.maps.ui.featurecontainer.VectorFeatureContainer.ClickEvent;
import org.vaadin.maps.ui.featurecontainer.VectorFeatureContainer.ClickListener;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.plugin.map.MapComponentFactory;
import com.tilioteo.hypothesis.plugin.map.MapUtility;
import com.tilioteo.hypothesis.plugin.map.SlideXmlConstants;
import com.tilioteo.hypothesis.plugin.map.SlideXmlUtility;
import com.tilioteo.hypothesis.plugin.map.event.VectorFeatureLayerData;
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
public class VectorFeatureLayer extends org.vaadin.maps.ui.layer.VectorFeatureLayer implements SlideComponent {
	
	private SlideManager slideManager;
	
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
			SlideComponent component = MapComponentFactory.createComponentFromElement(childElement, slideManager);
			if (component instanceof VectorFeature) {
				addComponent((VectorFeature)component);
			}
		}
	}

	@Override
	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);
		MapUtility utility = MapUtility.getInstance(slideManager);
		if (utility != null) {
			utility.setLayerProperties(this, element, properties);

			// set VectorFeatureLayer specific properties
			utility.setFeatureLayerProperties(this, element, properties);
		}
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
		AbstractBaseAction anonymousAction = SlideFactory.getInstance(slideManager).createAnonymousAction(element);
		if (anonymousAction != null)
			action = anonymousAction.getId();

		if (!Strings.isNullOrEmpty(action)) {
			if (name.equals(SlideXmlConstants.CLICK)) {
				setClickHandler(action);
			}
			// TODO add other event handlers
		}
	}

	private void setClickHandler(String actionId) {
		final VectorFeatureLayerData data = new VectorFeatureLayerData(this, slideManager);
		final Command componentEvent = MapComponentFactory.createVectorFeatureLayerClickEventCommand(data);
		final Command action = CommandFactory.createActionCommand(slideManager,	actionId, data);

		addClickListener(new ClickListener() {
			@Override
			public void click(ClickEvent event) {
				data.setXY(event.getRelativeX(), event.getRelativeY());
				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

}
