/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.ui;

import java.util.List;

import org.dom4j.Element;
import org.vaadin.maps.ui.tile.ImageTile.ClickEvent;
import org.vaadin.maps.ui.tile.ImageTile.ClickListener;
import org.vaadin.maps.ui.tile.ImageTile.LoadEvent;
import org.vaadin.maps.ui.tile.ImageTile.LoadListener;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.plugin.map.MapComponentFactory;
import com.tilioteo.hypothesis.plugin.map.MapUtility;
import com.tilioteo.hypothesis.plugin.map.event.ImageLayerData;
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
public class ImageLayer extends org.vaadin.maps.ui.layer.ImageLayer implements SlideComponent {

	private SlideManager slideManager;
	
	public ImageLayer() {
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
	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		MapUtility.setLayerProperties(this, element, properties);

		// set ImageLayer specific properties
		setTileUrl(properties.get(SlideXmlConstants.URL, ""));
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
			if (name.equals(SlideXmlConstants.CLICK)) {
				setClickHandler(action);
			} else if (name.equals(SlideXmlConstants.LOAD)) {
				setLoadHandler(action);
			}
			// TODO add other event handlers
		}
	}

	private void setClickHandler(String actionId) {
		final ImageLayerData data = new ImageLayerData(this, slideManager);
		final Command componentEvent = MapComponentFactory.createImageLayerClickEventCommand(data);
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId);

		addClickListener(new ClickListener() {
			@Override
			public void click(ClickEvent event) {
				data.setXY(event.getRelativeX(), event.getRelativeY());
				componentEvent.execute();
				action.execute();
			}
		});
	}

	private void setLoadHandler(String actionId) {
		final Command componentEvent = MapComponentFactory.createImageLayerLoadEventCommand(this, slideManager);
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId);
		
		addLoadListener(new LoadListener() {
			@Override
			public void load(LoadEvent event) {
				componentEvent.execute();
				action.execute();
			}
		});
	}

}