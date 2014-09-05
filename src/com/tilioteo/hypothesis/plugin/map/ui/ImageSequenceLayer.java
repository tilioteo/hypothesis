/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.ui;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.vaadin.maps.ui.tile.ImageSequenceTile.ChangeEvent;
import org.vaadin.maps.ui.tile.ImageSequenceTile.ChangeListener;
import org.vaadin.maps.ui.tile.ImageSequenceTile.ClickEvent;
import org.vaadin.maps.ui.tile.ImageSequenceTile.ClickListener;
import org.vaadin.maps.ui.tile.ImageSequenceTile.LoadEvent;
import org.vaadin.maps.ui.tile.ImageSequenceTile.LoadListener;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.plugin.map.MapComponentFactory;
import com.tilioteo.hypothesis.plugin.map.MapUtility;
import com.tilioteo.hypothesis.plugin.map.SlideXmlConstants;
import com.tilioteo.hypothesis.plugin.map.event.ImageSequenceLayerData;
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
public class ImageSequenceLayer extends org.vaadin.maps.ui.layer.ImageSequenceLayer implements SlideComponent {

	private SlideManager slideManager;
	
	private final ArrayList<String> imageTags = new ArrayList<String>();
	
	public ImageSequenceLayer() {
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

	public void addSource(String url, String tag) {
		addTileUrl(url);
		imageTags.add(tag != null ? tag : "");
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		MapUtility.setLayerProperties(this, element, properties);

		// set ImageSequenceLayer specific properties
		MapUtility.setImageSequenceLayerProperties(this, element, properties);
		addTileUrl(properties.get(SlideXmlConstants.URL, ""));
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
		AbstractBaseAction anonymousAction = SlideFactory.getInstatnce().createAnonymousAction(element);
		if (anonymousAction != null)
			action = anonymousAction.getId();

		if (!Strings.isNullOrEmpty(action)) {
			if (name.equals(SlideXmlConstants.CLICK)) {
				setClickHandler(action);
			} else if (name.equals(SlideXmlConstants.LOAD)) {
				setLoadHandler(action);
			} else if (name.equals(SlideXmlConstants.CHANGE)) {
				setChangeHandler(action);
			}
			// TODO add other event handlers
		}
	}

	private void setClickHandler(String actionId) {
		final ImageSequenceLayerData data = new ImageSequenceLayerData(this, slideManager);
		final Command componentEvent = MapComponentFactory.createImageSequenceLayerClickEventCommand(data);
		final Command action = CommandFactory.createActionCommand(slideManager,	actionId);

		addClickListener(new ClickListener() {
			
			@Override
			public void click(ClickEvent event) {
				data.setXY(event.getRelativeX(), event.getRelativeY());
				data.setImageIndex(event.getIndex());
				data.setImageTag(imageTags.get(event.getIndex()));
				componentEvent.execute();
				action.execute();
			}
		});
	}

	private void setLoadHandler(String actionId) {
		final Command componentEvent = MapComponentFactory.createImageSequenceLayerLoadEventCommand(this, slideManager);
		final Command action = CommandFactory.createActionCommand(slideManager,	actionId);
		
		addLoadListener(new LoadListener() {
			@Override
			public void load(LoadEvent event) {
				componentEvent.execute();
				action.execute();
			}
		});
	}

	private void setChangeHandler(String actionId) {
		final ImageSequenceLayerData data = new ImageSequenceLayerData(this, slideManager);
		final Command componentEvent = MapComponentFactory.createImageSequenceLayerChangeEventCommand(data);
		final Command action = CommandFactory.createActionCommand(slideManager,	actionId);
		
		addChangeListener(new ChangeListener() {
			@Override
			public void change(ChangeEvent event) {
				data.setImageIndex(event.getIndex());
				data.setImageTag(imageTags.get(event.getIndex()));
				componentEvent.execute();
				action.execute();
			}
		});
	}
}
