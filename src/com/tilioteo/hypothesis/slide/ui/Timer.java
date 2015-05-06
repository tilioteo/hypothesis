package com.tilioteo.hypothesis.slide.ui;

import java.util.List;

import org.dom4j.Element;
import org.vaadin.special.shared.ui.timer.TimerState.Direction;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.event.TimerData;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.vaadin.ui.Alignment;

@SuppressWarnings("serial")
public class Timer extends org.vaadin.special.ui.Timer implements SlideComponent {

	private SlideFascia slideFascia;
	
	public Timer() {
		super();
	}
	
	/**
	 * Not used, returns null
	 */
	@Override
	public Alignment getAlignment() {
		return null;
	}

	@Override
	public void setSlideManager(SlideFascia slideFascia) {
		this.slideFascia = slideFascia;
	}

	public void start() {
		start(getTime());
	}

	@Override
	public void loadFromXml(Element element) {

		setProperties(element);
		setHandlers(element);

	}

	protected void setProperties(Element element) {
		StringMap properties = SlideXmlUtility.getPropertyValueMap(element);
		
		setData(SlideXmlUtility.getId(element));
		setTime(properties.getInteger(SlideXmlConstants.TIME, 0));
		setDirection(directionFromString(properties.get(SlideXmlConstants.DIRECTION, "up")));
	}
	
	private Direction directionFromString(String value) {
		if ("up".equalsIgnoreCase(value)) {
			return Direction.UP;
		} else if ("down".equalsIgnoreCase(value)) {
			return Direction.DOWN;
		}
		return null;
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
			if (name.equals(SlideXmlConstants.START)) {
				setStartHandler(action);
			} else if (name.equals(SlideXmlConstants.STOP)) {
				setStopHandler(action);
			} else if (name.equals(SlideXmlConstants.UPDATE)) {
				setUpdateHandler(action, Strings.toInteger((element.attributeValue(SlideXmlConstants.INTERVAL))));
			}
			// TODO add other event handlers
		}
	}

	private void setStartHandler(final String actionId) {
		addStartListener(new StartListener() {
			@Override
			public void start(StartEvent event) {
				TimerData data = new TimerData(Timer.this, slideFascia);
				data.setTime(event.getTime());
				
				Command componentEvent = CommandFactory.createTimerStartEventCommand(data);
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
				TimerData data = new TimerData(Timer.this, slideFascia);
				data.setTime(event.getTime());

				Command componentEvent = CommandFactory.createTimerStopEventCommand(data);
				Command action = CommandFactory.createActionCommand(slideFascia, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	private void setUpdateHandler(final String actionId, Integer interval) {
		if (interval != null) {
			addUpdateListener(interval, new UpdateListener() {
				@Override
				public void update(UpdateEvent event) {
					TimerData data = new TimerData(Timer.this, slideFascia);
					data.setTime(event.getTime());

					Command componentEvent = CommandFactory.createTimerUpdateEventCommand(data);
					Command action = CommandFactory.createActionCommand(slideFascia, actionId, data);

					Command.Executor.execute(componentEvent);
					Command.Executor.execute(action);
				}
			});
		}
	}

}
