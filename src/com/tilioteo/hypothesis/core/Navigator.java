/**
 * 
 */
package com.tilioteo.hypothesis.core;

import com.tilioteo.hypothesis.event.FinishSlideEvent;
import com.tilioteo.hypothesis.event.FinishSlideEvent.Direction;
import com.tilioteo.hypothesis.event.ProcessEventBus;

/**
 * @author kamil
 *
 */
public class Navigator {
	
	private SlideManager slideManager;
	
	public Navigator(SlideManager slideManager) {
		this.slideManager = slideManager;
	}
	
	public void next() {
		if (slideManager.hasValidFields()) {
			ProcessEventBus.get().post(new FinishSlideEvent(slideManager.current(), Direction.NEXT));
		}
	}
	
	public void prior() {
		ProcessEventBus.get().post(new FinishSlideEvent(slideManager.current(), Direction.PRIOR));
	}

}
