/**
 * 
 */
package org.hypothesis.event.model;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ProcessEventTypes {

	private static long nextId = 0;

	private static final HashMap<String, ProcessEventType> events = new HashMap<String, ProcessEventType>();

	public static final String Null = null;// "";

	public static final String StartTest = "START_TEST";

	public static final String NextSlide = "NEXT_SLIDE";
	public static final String PriorSlide = "PRIOR_SLIDE";

	public static final String FinishSlide = "FINISH_SLIDE";

	public static final String NextTask = "NEXT_TASK";
	public static final String FinishTask = "FINISH_TASK";
	public static final String NextBranch = "NEXT_BRANCH";
	public static final String FinishBranch = "FINISH_BRANCH";
	public static final String FinishTest = "FINISH_TEST";
	public static final String BreakTest = "BREAK_TEST";
	public static final String ContinueTest = "CONTINUE_TEST";
	public static final String TestError = "TEST_ERROR";
	public static final String RenderSlide = "RENDER_SLIDE";
	public static final String AfterRender = "AFTER_RENDER";

	public static final String Action = "ACTION";

	public static final String ButtonClick = "BUTTON_CLICK";
	public static final String ButtonPanelClick = "BUTTONPANEL_CLICK";
	public static final String RadioButtonClick = "RADIOBUTTON_CLICK";
	public static final String CheckBoxClick = "CHECKBOX_CLICK";
	public static final String SelectPanelClick = "SELECTPANEL_CLICK";
	public static final String ImageClick = "IMAGE_CLICK";
	public static final String ImageLoad = "IMAGE_LOAD";
	public static final String ImageError = "IMAGE_ERROR";
	public static final String TimerStart = "TIMER_START";
	public static final String TimerStop = "TIMER_STOP";
	public static final String TimerUpdate = "TIMER_UPDATE";
	public static final String WindowInit = "WINDOW_INIT";
	public static final String WindowOpen = "WINDOW_OPEN";
	public static final String WindowClose = "WINDOW_CLOSE";
	public static final String VideoClick = "VIDEO_CLICK";
	public static final String VideoLoad = "VIDEO_LOAD";
	public static final String VideoStart = "VIDEO_START";
	public static final String VideoStop = "VIDEO_STOP";
	public static final String AudioLoad = "AUDIO_LOAD";
	public static final String AudioStart = "AUDIO_START";
	public static final String AudioStop = "AUDIO_STOP";
	public static final String SlideInit = "SLIDE_INIT";
	public static final String SlideShow = "SLIDE_SHOW";
	public static final String ShortcutKey = "SHORTCUT_KEY";
	public static final String Message = "MESSAGE";

	static {
		registerEvent(StartTest); // 1
		registerEvent(FinishSlide); // 2
		registerEvent(NextSlide); // 3
		registerEvent(PriorSlide); // 4
		registerEvent(FinishTask); // 5
		registerEvent(NextTask); // 6
		registerEvent(FinishBranch); // 7
		registerEvent(NextBranch); // 8
		registerEvent(FinishTest); // 9
		nextId += 5;
		registerEvent(BreakTest); // 15
		registerEvent(ContinueTest); // 16
		nextId += 3;
		registerEvent(TestError); // 20
		nextId += 9;
		registerEvent(RenderSlide); // 30
		registerEvent(AfterRender); // 31

		registerEvent(SlideInit); // 32
		registerEvent(SlideShow); // 33
		registerEvent(ShortcutKey); // 34
		registerEvent(Message); // 35
		nextId += 15;
		registerEvent(Action); // 50
		nextId += 49;
		registerEvent(ButtonClick); // 100
		registerEvent(RadioButtonClick); // 101
		registerEvent(CheckBoxClick); // 102
		registerEvent(ButtonPanelClick); // 103
		registerEvent(SelectPanelClick); // 104
		registerEvent(ImageClick); // 105
		registerEvent(ImageLoad); // 106
		registerEvent(TimerStart); // 107
		registerEvent(TimerStop); // 108
		registerEvent(TimerUpdate); // 109
		registerEvent(WindowInit); // 110
		registerEvent(WindowOpen); // 111
		registerEvent(WindowClose); // 112
		registerEvent(VideoClick); // 113
		registerEvent(VideoLoad); // 114
		registerEvent(VideoStart); // 115
		registerEvent(VideoStop); // 116
		registerEvent(AudioLoad); // 117
		registerEvent(AudioStart); // 118
		registerEvent(AudioStop); // 119
		registerEvent(ImageError); // 120
		// nextId += 880; // plugin events begin from 1000
	}

	private static long generateId() {
		return ++nextId;
	}

	public static ProcessEventType get(String key) {
		return events.get(key);
	}

	public static Long getFinishSlideEventId() {
		return get(FinishSlide).getId();
	}

	public static void registerEvent(String name) {
		if (!events.containsKey(name)) {
			ProcessEventType event = new ProcessEventType(generateId(), name);
			events.put(name, event);
		}
	}

	public static void registerPluginEvents(Set<String> names) {
		// increase next event id to begin in thousands
		nextId = (Math.round(Math.floor(nextId / 1000)) + 1) * 1000 - 1;

		for (String name : names) {
			registerEvent(name);
		}
	}

}
