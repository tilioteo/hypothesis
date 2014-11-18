/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.util.List;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.event.AudioData;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.tilioteo.hypothesis.shared.ui.audio.AudioServerRpc;
import com.tilioteo.hypothesis.shared.ui.audio.AudioState;
import com.tilioteo.hypothesis.ui.Media.CanPlayThroughEvent;
import com.tilioteo.hypothesis.ui.Media.CanPlayThroughListener;
import com.tilioteo.hypothesis.ui.Media.StartEvent;
import com.tilioteo.hypothesis.ui.Media.StartListener;
import com.tilioteo.hypothesis.ui.Media.StopEvent;
import com.tilioteo.hypothesis.ui.Media.StopListener;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Upload.StartedEvent;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class Audio extends com.vaadin.ui.Audio implements SlideComponent {
	
	protected AudioServerRpc rpc = new AudioServerRpc() {
		
		@Override
		public void stop(double time, boolean paused) {
			fireEvent(new StopEvent(Audio.this, time, paused));
		}
		
		@Override
		public void start(double time, boolean resumed) {
			fireEvent(new StartEvent(Audio.this, time, resumed));
		}
		
		@Override
		public void canPlayThrough() {
			fireEvent(new CanPlayThroughEvent(Audio.this));
		}
	};

	protected SlideManager slideManager;
	private ParentAlignment parentAlignment;


    @Override
    protected AudioState getState() {
        return (AudioState) super.getState();
    }

    public Audio() {
        this("", null);
    }

    /**
     * @param caption
     *            The caption of the audio component.
     */
    public Audio(String caption) {
        this(caption, null);
    }

    /**
     * @param caption
     *            The caption for the audio component.
     * @param source
     *            The audio file to play.
     */
    public Audio(String caption, Resource source) {
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
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonProperties(this, element, properties, parentAlignment);

		ComponentUtility.setMediaSources(this, element);
		
		// TODO make localizable
		setAltText("Your browser doesn't support audio.");
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
			if (name.equals(SlideXmlConstants.LOAD)) {
				setLoadHandler(action);
			} else if (name.equals(SlideXmlConstants.START)) {
				setStartHandler(action);
			} else if (name.equals(SlideXmlConstants.STOP)) {
				setStopHandler(action);
			}
			// TODO add other event handlers
		}
	}

	private void setLoadHandler(String actionId) {
		final AudioData data = new AudioData(this, slideManager);
		final Command componentEvent = CommandFactory.createAudioLoadEventCommand(data);
		final Command action = CommandFactory.createActionCommand(slideManager,
				actionId, data);

		addCanPlayThroughListener(new CanPlayThroughListener() {
			@Override
			public void canPlayThrough(CanPlayThroughEvent event) {
				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	private void setStartHandler(String actionId) {
		final AudioData data = new AudioData(this, slideManager);
		final Command componentEvent = CommandFactory.createAudioStartEventCommand(data);
		final Command action = CommandFactory.createActionCommand(slideManager,	actionId, data);

		addStartListener(new StartListener() {
			@Override
			public void start(StartEvent event) {
				data.setTime(event.getTime());
				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	private void setStopHandler(String actionId) {
		final AudioData data = new AudioData(this, slideManager);
		final Command componentEvent = CommandFactory.createAudioStopEventCommand(data);
		final Command action = CommandFactory.createActionCommand(slideManager,	actionId, data);

		addStopListener(new StopListener() {
			@Override
			public void stop(StopEvent event) {
				data.setTime(event.getTime());
				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	@Override
	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
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

}
