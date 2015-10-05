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

import com.tilioteo.common.Strings;
import com.tilioteo.common.collections.StringMap;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.plugin.map.MapComponentFactory;
import com.tilioteo.hypothesis.plugin.map.MapUtility;
import com.tilioteo.hypothesis.plugin.map.SlideXmlConstants;
import com.tilioteo.hypothesis.plugin.map.event.ImageSequenceLayerData;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.vaadin.ui.Alignment;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ImageSequenceLayer extends org.vaadin.maps.ui.layer.ImageSequenceLayer implements SlideComponent {

	private SlideFascia slideFascia;
	
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
	public void setSlideManager(SlideFascia slideFascia) {
		this.slideFascia = slideFascia;
	}

	public void addSource(String url, String tag) {
		addTileUrl(url);
		imageTags.add(tag != null ? tag : "");
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideXmlUtility.getPropertyValueMap(element);
		MapUtility utility = MapUtility.getInstance(slideFascia);
		if (utility != null) {
			utility.setLayerProperties(this, element, properties);

			// set ImageSequenceLayer specific properties
			utility.setImageSequenceLayerProperties(this, element, properties);
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

	private void setClickHandler(final String actionId) {
		addClickListener(new ClickListener() {
			
			@Override
			public void click(ClickEvent event) {
				ImageSequenceLayerData data = new ImageSequenceLayerData(ImageSequenceLayer.this, slideFascia);
				data.setXY(event.getRelativeX(), event.getRelativeY());
				data.setImageIndex(event.getIndex());
				data.setImageTag(imageTags.get(event.getIndex()));

				Command componentEvent = MapComponentFactory.createImageSequenceLayerClickEventCommand(data,
						event.getServerDatetime(), event.getClientDatetime());
				Command action = CommandFactory.createActionCommand(slideFascia, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	private void setLoadHandler(final String actionId) {
		addLoadListener(new LoadListener() {
			@Override
			public void load(LoadEvent event) {
				ImageSequenceLayerData data = new ImageSequenceLayerData(ImageSequenceLayer.this, slideFascia);
				Command componentEvent = MapComponentFactory.createImageSequenceLayerLoadEventCommand(data,
						event.getServerDatetime(), event.getClientDatetime());
				Command action = CommandFactory.createActionCommand(slideFascia, actionId, data);
				
				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	private void setChangeHandler(final String actionId) {
		addChangeListener(new ChangeListener() {
			@Override
			public void change(ChangeEvent event) {
				ImageSequenceLayerData data = new ImageSequenceLayerData(ImageSequenceLayer.this, slideFascia);
				data.setImageIndex(event.getIndex());
				data.setImageTag(imageTags.get(event.getIndex()));

				Command componentEvent = MapComponentFactory.createImageSequenceLayerChangeEventCommand(data,
						event.getServerDatetime(), event.getClientDatetime());
				Command action = CommandFactory.createActionCommand(slideFascia, actionId, data);
				
				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}
}
