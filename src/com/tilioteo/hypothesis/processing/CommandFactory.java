/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.core.SlideManager;
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
import com.tilioteo.hypothesis.event.SelectPanelData;
import com.tilioteo.hypothesis.event.SelectPanelEvent;
import com.tilioteo.hypothesis.event.TimerData;
import com.tilioteo.hypothesis.event.TimerEvent;
import com.tilioteo.hypothesis.event.VideoData;
import com.tilioteo.hypothesis.event.VideoEvent;
import com.tilioteo.hypothesis.event.WindowData;
import com.tilioteo.hypothesis.event.WindowEvent;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class CommandFactory {

	private static Logger log = Logger.getLogger(CommandFactory.class);

	/*public static Command createActionCommand(final SlideManager slideManager, String actionId) {
		return createActionCommand(slideManager, actionId, null);
	}*/

	public static Command createActionCommand(final SlideManager slideManager, String actionId, final AbstractComponentData<?> data) {
		final AbstractBaseAction action = slideManager != null ? slideManager.getActions().get(actionId) : null;

		return new Command() {
			public void execute() {
				if (action != null) {
					log.debug("Execute action command.");
					slideManager.addComponentDataVariable(data);
					action.execute();
					slideManager.clearComponentDataVariable();
				}
			}
		};
	}

	public static Command createComponentEventCommand(final AbstractComponentEvent<?> event) {
		return new Command() {
			public void execute() {
				log.debug("Execute component event command.");
				event.getComponentData().getSlideManager().getEventManager().fireEvent(event);
			}
		};
	}

	public static Command createButtonClickEventCommand(ButtonData data) {
		ButtonEvent event = new ButtonEvent.Click(data);

		return createComponentEventCommand(event);
	}

	public static Command createButtonPanelClickEventCommand(ButtonPanelData data) {
		ButtonPanelEvent event = new ButtonPanelEvent.Click(data);

		return createComponentEventCommand(event);
	}

	public static Command createImageClickEventCommand(ImageData data) {
		ImageEvent event = new ImageEvent.Click(data);

		return createComponentEventCommand(event);
	}

	public static Command createImageLoadEventCommand(ImageData data) {
		ImageEvent event = new ImageEvent.Load(data);

		return createComponentEventCommand(event);
	}

	public static Command createSelectPanelClickEventCommand(SelectPanelData data) {
		SelectPanelEvent event = new SelectPanelEvent.Click(data);

		return createComponentEventCommand(event);
	}

	public static Command createTimerStartEventCommand(TimerData data) {
		TimerEvent event = new TimerEvent.Start(data);

		return createComponentEventCommand(event);
	}

	public static Command createTimerStopEventCommand(TimerData data) {
		TimerEvent event = new TimerEvent.Stop(data);

		return createComponentEventCommand(event);
	}

	public static Command createTimerUpdateEventCommand(TimerData data) {
		TimerEvent event = new TimerEvent.Update(data);

		return createComponentEventCommand(event);
	}

	public static Command createVideoClickEventCommand(VideoData data) {
		VideoEvent event = new VideoEvent.Click(data);

		return createComponentEventCommand(event);
	}

	public static Command createVideoLoadEventCommand(VideoData data) {
		VideoEvent event = new VideoEvent.Load(data);

		return createComponentEventCommand(event);
	}

	public static Command createVideoStartEventCommand(VideoData data) {
		VideoEvent event = new VideoEvent.Start(data);

		return createComponentEventCommand(event);
	}

	public static Command createVideoStopEventCommand(VideoData data) {
		VideoEvent event = new VideoEvent.Stop(data);

		return createComponentEventCommand(event);
	}

	public static Command createAudioLoadEventCommand(AudioData data) {
		AudioEvent event = new AudioEvent.Load(data);

		return createComponentEventCommand(event);
	}

	public static Command createAudioStartEventCommand(AudioData data) {
		AudioEvent event = new AudioEvent.Start(data);

		return createComponentEventCommand(event);
	}

	public static Command createAudioStopEventCommand(AudioData data) {
		AudioEvent event = new AudioEvent.Stop(data);

		return createComponentEventCommand(event);
	}

	public static Command createWindowInitEventCommand(WindowData data) {
		WindowEvent event = new WindowEvent.Init(data);

		return createComponentEventCommand(event);
	}

	public static Command createWindowOpenEventCommand(WindowData data) {
		WindowEvent event = new WindowEvent.Open(data);

		return createComponentEventCommand(event);
	}

	public static Command createWindowCloseEventCommand(WindowData data) {
		WindowEvent event = new WindowEvent.Close(data);

		return createComponentEventCommand(event);
	}

}
