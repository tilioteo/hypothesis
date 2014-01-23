/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.util.HashMap;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ProcessEventTypes {

	private static int nextId = 0;

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
	public static final String ButtonClick = "BUTTON_CLICK";
	public static final String ButtonPanelClick = "BUTTONPANEL_CLICK";
	public static final String RadioButtonClick = "RADIOBUTTON_CLICK";

	public static final String RadioPanelClick = "RADIOPANEL_CLICK";
	public static final String ImageClick = "IMAGE_CLICK";
	public static final String ImageLoad = "IMAGE_LOAD";

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
		nextId += 4;
		registerEvent(TestError); // 20
		nextId += 9;
		registerEvent(RenderSlide); // 30
		registerEvent(AfterRender); // 31
		nextId += 68;
		registerEvent(ButtonClick); // 100
		registerEvent(ButtonPanelClick); // 101
		registerEvent(RadioButtonClick); // 102
		registerEvent(RadioPanelClick); // 103
		registerEvent(ImageClick); // 104
		registerEvent(ImageLoad); // 105
		nextId += 894; // user events begin from 1000
	}

	private static int generateId() {
		return ++nextId;
	}

	public static ProcessEventType get(String key) {
		return events.get(key);
	}

	public static void registerEvent(String name) {
		if (!events.containsKey(name)) {
			ProcessEventType event = new ProcessEventType(generateId(), name);
			events.put(name, event);
		}
	}

}
