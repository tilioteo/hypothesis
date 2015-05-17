/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.io.Serializable;

import com.tilioteo.hypothesis.event.FinishSlideEvent;
import com.tilioteo.hypothesis.event.FinishSlideEvent.Direction;
import com.tilioteo.hypothesis.event.ProcessEventBus;
import com.tilioteo.hypothesis.interfaces.SlideFascia;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class SlideNavigator implements Serializable {
	
	private SlideFascia slideFascia;
	
	public SlideNavigator(SlideFascia slideFascia) {
		this.slideFascia = slideFascia;
	}
	
	public void next() {
		if (slideFascia.hasValidFields()) {
			ProcessEventBus.get().post(new FinishSlideEvent(Direction.NEXT));
		}
	}
	
	public void prior() {
		ProcessEventBus.get().post(new FinishSlideEvent(Direction.PRIOR));
	}

}
