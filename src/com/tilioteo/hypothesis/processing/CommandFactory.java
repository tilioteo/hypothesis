/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;

import com.tilioteo.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.event.AudioData;
import com.tilioteo.hypothesis.event.AudioEvent;
import com.tilioteo.hypothesis.event.ButtonData;
import com.tilioteo.hypothesis.event.ButtonEvent;
import com.tilioteo.hypothesis.event.ButtonPanelData;
import com.tilioteo.hypothesis.event.ButtonPanelEvent;
import com.tilioteo.hypothesis.event.ImageData;
import com.tilioteo.hypothesis.event.ImageEvent;
import com.tilioteo.hypothesis.event.ProcessEventBus;
import com.tilioteo.hypothesis.event.SelectPanelData;
import com.tilioteo.hypothesis.event.SelectPanelEvent;
import com.tilioteo.hypothesis.event.SlideData;
import com.tilioteo.hypothesis.event.SlideEvent;
import com.tilioteo.hypothesis.event.TimerData;
import com.tilioteo.hypothesis.event.TimerEvent;
import com.tilioteo.hypothesis.event.VideoData;
import com.tilioteo.hypothesis.event.VideoEvent;
import com.tilioteo.hypothesis.event.WindowData;
import com.tilioteo.hypothesis.event.WindowEvent;
import com.tilioteo.hypothesis.interfaces.Action;
import com.tilioteo.hypothesis.interfaces.SlideFascia;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class CommandFactory implements Serializable {

	private static Logger log = Logger.getLogger(CommandFactory.class);

	public static Command createActionCommand(final SlideFascia slideFascia, final String actionId, final AbstractComponentData<?> data) {
		if (slideFascia != null) {
			if (!Strings.isNullOrEmpty(actionId)) {
				return new Command() {
					public void execute() {
						Action action = slideFascia.getAction(actionId);
						if (action != null) {
							log.debug("Execute action command.");
							SlideFactory.getInstance(slideFascia).addComponentDataVariable(data);
							action.execute();
							SlideFactory.getInstance(slideFascia).clearComponentDataVariable();
						} else {
							log.error("Action " + actionId + " IS NULL!");
						}
					}
				};
			} else {
				log.error("createActionCommand: actionId IS NULL OR EMPTY!");
			}
		} else {
			log.error("createActionCommand: slideFascia IS NULL!");
		}
		
		return null;
	}

	public static Command createSlideActionCommand(final SlideFascia slideManager, final String actionId, final SlideData data) {
		if (slideManager != null) {
			if (!Strings.isNullOrEmpty(actionId)) {
				return new Command() {
					public void execute() {
						Action action = slideManager.getAction(actionId);
						if (action != null) {
							log.debug("Execute action command.");
							SlideFactory.getInstance(slideManager).addComponentDataVariable(data);
							action.execute();
							SlideFactory.getInstance(slideManager).clearComponentDataVariable();
						} else {
							log.error("Action " + actionId + " IS NULL!");
						}
					}
				};
			} else {
				log.error("createActionCommand: actionId IS NULL OR EMPTY!");
			}
		} else {
			log.error("createActionCommand: slideFascia IS NULL!");
		}
		
		return null;
	}

	public static Command createComponentEventCommand(final AbstractComponentEvent<?> event) {
		return new Command() {
			public void execute() {
				log.debug("Execute component event command.");
				ProcessEventBus.get(event.getComponentData().getSender().getUI()).post(event);
			}
		};
	}

	public static Command createSlideEventCommand(final SlideEvent event) {
		return new Command() {
			public void execute() {
				log.debug("Execute slide event command.");
				ProcessEventBus.get(event.getComponentData().getSlideManager().getUI()).post(event);
			}
		};
	}

	public static Command createButtonClickEventCommand(ButtonData data, Date timestamp, Date clientTimestamp) {
		ButtonEvent event = new ButtonEvent.Click(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return createComponentEventCommand(event);
	}

	public static Command createButtonPanelClickEventCommand(ButtonPanelData data, Date timestamp, Date clientTimestamp) {
		ButtonPanelEvent event = new ButtonPanelEvent.Click(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return createComponentEventCommand(event);
	}

	public static Command createImageClickEventCommand(ImageData data, Date timestamp, Date clientTimestamp) {
		ImageEvent event = new ImageEvent.Click(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return createComponentEventCommand(event);
	}

	public static Command createImageLoadEventCommand(ImageData data, Date timestamp, Date clientTimestamp) {
		ImageEvent event = new ImageEvent.Load(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return createComponentEventCommand(event);
	}

	public static Command createImageErrorEventCommand(ImageData data, Date timestamp, Date clientTimestamp) {
		ImageEvent event = new ImageEvent.Error(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return createComponentEventCommand(event);
	}

	public static Command createSelectPanelClickEventCommand(SelectPanelData data, Date timestamp, Date clientTimestamp) {
		SelectPanelEvent event = new SelectPanelEvent.Click(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return createComponentEventCommand(event);
	}

	public static Command createTimerStartEventCommand(TimerData data/*, Date timestamp*/) {
		TimerEvent event = new TimerEvent.Start(data);
		//event.setTimestamp(timestamp);

		return createComponentEventCommand(event);
	}

	public static Command createTimerStopEventCommand(TimerData data/*, Date timestamp*/) {
		TimerEvent event = new TimerEvent.Stop(data);
		//event.setTimestamp(timestamp);

		return createComponentEventCommand(event);
	}

	public static Command createTimerUpdateEventCommand(TimerData data/*, Date timestamp*/) {
		TimerEvent event = new TimerEvent.Update(data);
		//event.setTimestamp(timestamp);

		return createComponentEventCommand(event);
	}

	public static Command createVideoClickEventCommand(VideoData data, Date timestamp, Date clientTimestamp) {
		VideoEvent event = new VideoEvent.Click(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return createComponentEventCommand(event);
	}

	public static Command createVideoLoadEventCommand(VideoData data, Date timestamp, Date clientTimestamp) {
		VideoEvent event = new VideoEvent.Load(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return createComponentEventCommand(event);
	}

	public static Command createVideoStartEventCommand(VideoData data, Date timestamp, Date clientTimestamp) {
		VideoEvent event = new VideoEvent.Start(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return createComponentEventCommand(event);
	}

	public static Command createVideoStopEventCommand(VideoData data, Date timestamp, Date clientTimestamp) {
		VideoEvent event = new VideoEvent.Stop(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return createComponentEventCommand(event);
	}

	public static Command createAudioLoadEventCommand(AudioData data, Date timestamp, Date clientTimestamp) {
		AudioEvent event = new AudioEvent.Load(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return createComponentEventCommand(event);
	}

	public static Command createAudioStartEventCommand(AudioData data, Date timestamp, Date clientTimestamp) {
		AudioEvent event = new AudioEvent.Start(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return createComponentEventCommand(event);
	}

	public static Command createAudioStopEventCommand(AudioData data, Date timestamp, Date clientTimestamp) {
		AudioEvent event = new AudioEvent.Stop(data);
		event.setTimestamp(timestamp);
		event.setClientTimestamp(clientTimestamp);

		return createComponentEventCommand(event);
	}

	public static Command createWindowInitEventCommand(WindowData data/*, Date timestamp*/) {
		WindowEvent event = new WindowEvent.Init(data);
		//event.setTimestamp(timestamp);

		return createComponentEventCommand(event);
	}

	public static Command createWindowOpenEventCommand(WindowData data/*, Date timestamp*/) {
		WindowEvent event = new WindowEvent.Open(data);
		//event.setTimestamp(timestamp);

		return createComponentEventCommand(event);
	}

	public static Command createWindowCloseEventCommand(WindowData data/*, Date timestamp*/) {
		WindowEvent event = new WindowEvent.Close(data);
		//event.setTimestamp(timestamp);

		return createComponentEventCommand(event);
	}

	public static Command createSlideInitEventCommand(SlideData data, Date timestamp) {
		SlideEvent event = new SlideEvent.Init(data);
		event.setTimestamp(timestamp);

		return createSlideEventCommand(event);
	}

	public static Command createSlideShowEventCommand(SlideData data, Date timestamp) {
		SlideEvent event = new SlideEvent.Show(data);
		event.setTimestamp(timestamp);

		return createSlideEventCommand(event);
	}

	public static Command createSlideShortcutKeyEventCommand(SlideData data/*, Date timestamp*/) {
		SlideEvent event = new SlideEvent.ShortcutKey(data);
		//event.setTimestamp(timestamp);

		return createSlideEventCommand(event);
	}

	public static Command createMessageEventCommand(SlideData data, Date timestamp) {
		SlideEvent event = new SlideEvent.Message(data);
		event.setTimestamp(timestamp);

		return createSlideEventCommand(event);
	}

}
