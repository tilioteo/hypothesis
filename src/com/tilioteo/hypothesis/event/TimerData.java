/**
 * 
 */
package com.tilioteo.hypothesis.event;

import org.dom4j.Element;

import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.slide.ui.Timer;

/**
 * @author kamil
 *
 */
public class TimerData extends AbstractComponentData<Timer> {

	private long time;

	public TimerData(Timer sender, SlideFascia slideFascia) {
		super(sender, slideFascia);
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
