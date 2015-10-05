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

import com.tilioteo.common.Strings;
import com.tilioteo.common.collections.StringMap;
import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.event.AudioData;
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
public class Audio extends org.vaadin.special.ui.Audio implements SlideComponent {
	
	protected SlideFascia slideFascia;
	private ParentAlignment parentAlignment;

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
		
		setAltText(Messages.getString("Message.Error.AudioSupport"));
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

	private void setLoadHandler(final String actionId) {
		addCanPlayThroughListener(new CanPlayThroughListener() {
			@Override
			public void canPlayThrough(CanPlayThroughEvent event) {
				AudioData data = new AudioData(Audio.this, slideFascia);
				Command componentEvent = CommandFactory.createAudioLoadEventCommand(data, event.getServerDatetime(), event.getClientDatetime());
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
				AudioData data = new AudioData(Audio.this, slideFascia);
				data.setTime(event.getMediaTime());

				Command componentEvent = CommandFactory.createAudioStartEventCommand(data, event.getServerDatetime(), event.getClientDatetime());
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
				AudioData data = new AudioData(Audio.this, slideFascia);
				data.setTime(event.getMediaTime());

				Command componentEvent = CommandFactory.createAudioStopEventCommand(data, event.getServerDatetime(), event.getClientDatetime());
				Command action = CommandFactory.createActionCommand(slideFascia,	actionId, data);

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
