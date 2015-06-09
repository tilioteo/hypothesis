/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

import java.util.List;

import org.dom4j.Element;
import org.vaadin.special.ui.Media.CanPlayThroughEvent;
import org.vaadin.special.ui.Media.CanPlayThroughListener;
import org.vaadin.special.ui.Media.StartEvent;
import org.vaadin.special.ui.Media.StartListener;
import org.vaadin.special.ui.Media.StopEvent;
import org.vaadin.special.ui.Media.StopListener;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.event.VideoData;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class Video extends org.vaadin.special.ui.Video implements SlideComponent, Maskable {
	
	protected SlideFascia slideFascia;
	private ParentAlignment parentAlignment;
	private Mask mask = null;

    public Video() {
    	this("", null);
    }

    /**
     * @param caption
     *            The caption for this video.
     */
    public Video(String caption) {
    	this(caption, null);
    }

    /**
     * @param caption
     *            The caption for this video.
     * @param source
     *            The Resource containing the video to play.
     */
    public Video(String caption, Resource source) {
    	super(caption, source);

        parentAlignment = new ParentAlignment();
    }

	@Override
	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	@Override
	public void loadFromXml(Element element) {
		setProperties(element);
		setHandlers(element);
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideXmlUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonProperties(this, element, properties, parentAlignment);

		ComponentUtility.setMediaSources(this, element);
		
		// TODO make localizable
		setAltText("Your browser doesn't support video.");
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
			} else if (name.equals(SlideXmlConstants.START)) {
				setStartHandler(action);
			} else if (name.equals(SlideXmlConstants.STOP)) {
				setStopHandler(action);
			}
			// TODO add other event handlers
		}
	}

	private void setClickHandler(final String actionId) {
		addClickListener(new Video.ClickListener() {
			@Override
			public void click(Video.ClickEvent event) {
				VideoData data = new VideoData(Video.this, slideFascia);
				data.setXY(event.getRelativeX(), event.getRelativeY());
				data.setTime(event.getMediaTime());

				Command componentEvent = CommandFactory.createVideoClickEventCommand(data, event.getServerDatetime(), event.getClientDatetime());
				Command action = CommandFactory.createActionCommand(slideFascia, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	private void setLoadHandler(final String actionId) {
		addCanPlayThroughListener(new CanPlayThroughListener() {
			@Override
			public void canPlayThrough(CanPlayThroughEvent event) {
				VideoData data = new VideoData(Video.this, slideFascia);
				Command componentEvent = CommandFactory.createVideoLoadEventCommand(data, event.getServerDatetime(), event.getClientDatetime());
				Command action = CommandFactory.createActionCommand(slideFascia, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	private void setStartHandler(final String actionId) {
		addStartListener(new StartListener() {
			@Override
			public void start(StartEvent event) {
				VideoData data = new VideoData(Video.this, slideFascia);
				data.setTime(event.getMediaTime());

				Command componentEvent = CommandFactory.createVideoStartEventCommand(data, event.getServerDatetime(), event.getClientDatetime());
				Command action = CommandFactory.createActionCommand(slideFascia, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	private void setStopHandler(final String actionId) {
		addStopListener(new StopListener() {
			@Override
			public void stop(StopEvent event) {
				VideoData data = new VideoData(Video.this, slideFascia);
				data.setTime(event.getMediaTime());

				Command componentEvent = CommandFactory.createVideoStopEventCommand(data, event.getServerDatetime(), event.getClientDatetime());
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

}

