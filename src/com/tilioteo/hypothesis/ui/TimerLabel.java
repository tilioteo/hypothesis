/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.core.SlideUtility;
import com.tilioteo.hypothesis.shared.ui.timerlabel.TimerLabelState;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings({ "serial" })
public class TimerLabel extends Label implements SlideComponent {
	
	private SlideManager slideManager;
	private ParentAlignment parentAlignment;
	
	private Timer timer = null;

	public TimerLabel() {
		super();
		this.parentAlignment = new ParentAlignment();
	}
	
	public TimerLabel(SlideManager slideManager) {
		this();
		this.slideManager = slideManager;
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
	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	@Override
	public void loadFromXml(Element element) {

		setProperties(element);

	}

	protected void setProperties(Element element) {
		StringMap properties = SlideUtility.getPropertyValueMap(element);

		ComponentUtility.setCommonProperties(this, element, properties,
				parentAlignment);

		// TimerLabel specific properties
		
	}
	
	public Timer getTimer() {
		return timer;
	}
	
	public void setTimer(Timer timer) {
		//if (this.timer != timer) {
			//unregisterTimer();
			
			this.timer = timer;
			getState().timer = timer;

			//registerTimer();
		//}
	}

	/*private void registerTimer() {
		if (timer != null) {
			
		}
	}

	private void unregisterTimer() {
		if (timer != null) {
			
		}
	}*/

	@Override
	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

}
