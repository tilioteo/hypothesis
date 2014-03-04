/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.shared.ui.timerlabel.TimerLabelState;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings({ "serial" })
public class TimerLabel extends Label {
	
	public static final String DEAFAULT_TIME_FORMAT = "HH:mm:ss.S";

	private Timer timer = null;

	public TimerLabel() {
		super();
	}

	public TimerLabel(SlideManager slideManager) {
		super(slideManager);
	}

	public String getTimeFormat() {
		return getState().timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		getState().timeFormat = timeFormat;
	}

	@Override
	public TimerLabelState getState() {
		return (TimerLabelState) super.getState();
	}

	@Override
	protected void setProperties(Element element) {
		super.setProperties(element);
		
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		// TimerLabel specific properties
		setTimeFormat(properties.get(SlideXmlConstants.TIME_FORMAT, DEAFAULT_TIME_FORMAT));
		setTimer(slideManager.getTimer(properties.get(SlideXmlConstants.TIMER_ID)));
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
		getState().timer = timer;
	}

}
