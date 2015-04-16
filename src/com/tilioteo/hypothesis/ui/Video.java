/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.event.VideoData;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.tilioteo.hypothesis.shared.ui.video.VideoServerRpc;
import com.tilioteo.hypothesis.shared.ui.video.VideoState;
import com.tilioteo.hypothesis.ui.Media.CanPlayThroughEvent;
import com.tilioteo.hypothesis.ui.Media.CanPlayThroughListener;
import com.tilioteo.hypothesis.ui.Media.StartEvent;
import com.tilioteo.hypothesis.ui.Media.StartListener;
import com.tilioteo.hypothesis.ui.Media.StopEvent;
import com.tilioteo.hypothesis.ui.Media.StopListener;
import com.vaadin.event.ConnectorEventListener;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.Resource;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.util.ReflectTools;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class Video extends com.vaadin.ui.Video implements SlideComponent, Maskable {
	
	private static Logger log = Logger.getLogger(Video.class);

	protected VideoServerRpc rpc = new VideoServerRpc() {
		@Override
		public void click(MouseEventDetails mouseDetails, double time) {
			log.debug("VideoServerRpc: click()");
            fireEvent(new ClickEvent(Video.this, mouseDetails, time));
		}

		@Override
		public void start(double time, boolean resumed) {
			log.debug("VideoServerRpc: start()");
			fireEvent(new StartEvent(Video.this, time, resumed));
		}

		@Override
		public void stop(double time, boolean paused) {
			log.debug("VideoServerRpc: stop()");
			fireEvent(new StopEvent(Video.this, time, paused));
		}

		@Override
		public void canPlayThrough() {
			log.debug("VideoServerRpc: canPlayThrough()");
			fireEvent(new CanPlayThroughEvent(Video.this));
		}
	};

	protected SlideManager slideManager;
	private ParentAlignment parentAlignment;
	private Mask mask = null;


    @Override
    protected VideoState getState() {
        return (VideoState) super.getState();
    }

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
    	registerRpc(rpc);
        setShowControls(false);

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
		AbstractBaseAction anonymousAction = SlideFactory.getInstance(slideManager).createAnonymousAction(element);
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
				VideoData data = new VideoData(Video.this, slideManager);
				data.setXY(event.getRelativeX(), event.getRelativeY());
				data.setTime(event.getTime());

				Command componentEvent = CommandFactory.createVideoClickEventCommand(data);
				Command action = CommandFactory.createActionCommand(slideManager, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	private void setLoadHandler(final String actionId) {
		addCanPlayThroughListener(new CanPlayThroughListener() {
			@Override
			public void canPlayThrough(CanPlayThroughEvent event) {
				VideoData data = new VideoData(Video.this, slideManager);
				Command componentEvent = CommandFactory.createVideoLoadEventCommand(data);
				Command action = CommandFactory.createActionCommand(slideManager, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	private void setStartHandler(final String actionId) {
		addStartListener(new StartListener() {
			@Override
			public void start(StartEvent event) {
				VideoData data = new VideoData(Video.this, slideManager);
				data.setTime(event.getTime());

				Command componentEvent = CommandFactory.createVideoStartEventCommand(data);
				Command action = CommandFactory.createActionCommand(slideManager, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	private void setStopHandler(final String actionId) {
		addStopListener(new StopListener() {
			@Override
			public void stop(StopEvent event) {
				VideoData data = new VideoData(Video.this, slideManager);
				data.setTime(event.getTime());

				Command componentEvent = CommandFactory.createVideoStopEventCommand(data);
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
	
    public class ClickEvent extends MouseEvents.ClickEvent {
    	
    	private double time;

		public ClickEvent(Component source, MouseEventDetails mouseEventDetails, double time) {
			super(source, mouseEventDetails);
			
			this.time = time;
		}
		
		public double getTime() {
			return time;
		}
    }

    /**
     * Interface for listening for a {@link ClickEvent} fired by a
     * {@link Component}.
     * 
     */
    public interface ClickListener extends ConnectorEventListener {

        public static final Method clickMethod = ReflectTools.findMethod(
                ClickListener.class, "click", ClickEvent.class);

        /**
         * Called when a {@link Component} has been clicked. A reference to the
         * component is given by {@link ClickEvent#getComponent()}.
         * 
         * @param event
         *            An event containing information about the click.
         */
        public void click(ClickEvent event);
    }

    /**
     * Add a click listener to the component. The listener is called whenever
     * the user clicks inside the component. Depending on the content the event
     * may be blocked and in that case no event is fired.
     * 
     * Use {@link #removeClickListener(ClickListener)} to remove the listener.
     * 
     * @param listener
     *            The listener to add
     */
    public void addClickListener(ClickListener listener) {
        addListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class, listener,
                ClickListener.clickMethod);
    }

    /**
     * Remove a click listener from the component. The listener should earlier
     * have been added using {@link #addClickListener(ClickListener)}.
     * 
     * @param listener
     *            The listener to remove
     */
    public void removeClickListener(ClickListener listener) {
        removeListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class,
                listener);
    }
    
	public void addCanPlayThroughListener(CanPlayThroughListener listener) {
		addListener(CanPlayThroughEvent.EVENT_ID, CanPlayThroughEvent.class, listener,
				CanPlayThroughListener.MEDIA_CAN_PLAY_THROUGH);
	}

	public void removeCanPlayThroughListener(CanPlayThroughListener listener) {
		removeListener(CanPlayThroughEvent.EVENT_ID, CanPlayThroughEvent.class, listener);
	}

	public void addStartListener(StartListener listener) {
		addListener(StartEvent.EVENT_ID, StartEvent.class, listener,
				StartListener.MEDIA_START_METHOD);
	}

	public void removeStartListener(StartListener listener) {
		removeListener(StartEvent.EVENT_ID, StartedEvent.class, listener);
	}

	public void addStopListener(StopListener listener) {
		addListener(StopEvent.EVENT_ID, StopEvent.class, listener,
				StopListener.MEDIA_STOP_METHOD);
	}

	public void removeStopListener(StopListener listener) {
		removeListener(StopEvent.EVENT_ID, StopEvent.class, listener);
	}

	@Override
	public void mask() {
		if (null == mask) {
			mask = Mask.addToComponent(this);
		}
		mask.show();
	}

	@Override
	public void unmask() {
		if (mask != null) {
			mask.hide();
		}
	}
	
}

