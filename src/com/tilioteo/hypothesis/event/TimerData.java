/**
 * 
 */
package com.tilioteo.hypothesis.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.ui.Timer;

/**
 * @author kamil
 *
 */
public class TimerData extends AbstractComponentData<Timer> {

	private long time;

	public TimerData(Timer sender, SlideManager slideManager) {
		super(sender, slideManager);
	}

	public final long getTime() {
		return time;
	}

	public final void setTime(long time) {
		this.time = time;
	}

	@Override
	public void writeDataToElement(Element element) {
		SlideFactory.writeTimerData(element, this);
	}
}
