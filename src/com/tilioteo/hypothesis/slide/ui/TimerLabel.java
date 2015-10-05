/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

import org.dom4j.Element;

import com.tilioteo.common.collections.StringMap;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.slide.shared.ui.timerlabel.TimerLabelState;

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
		
		StringMap properties = SlideXmlUtility.getPropertyValueMap(element);

		// TimerLabel specific properties
		setTimeFormat(properties.get(SlideXmlConstants.TIME_FORMAT, DEAFAULT_TIME_FORMAT));
		SlideComponent component = slideFascia.getTimer(properties.get(SlideXmlConstants.TIMER_ID));
		if (component instanceof Timer) {
			setTimer((Timer)component);
		}
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
		getState().timer = timer;
	}

}
