/**
 * 
 */
package com.tilioteo.hypothesis.core;

import com.tilioteo.hypothesis.event.FinishSlideEvent;
import com.tilioteo.hypothesis.event.FinishSlideEvent.Direction;

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
		slideManager.getEventManager().fireEvent(
				new FinishSlideEvent(slideManager.current(), Direction.NEXT));
	}
	
	public void prior() {
		slideManager.getEventManager().fireEvent(
				new FinishSlideEvent(slideManager.current(), Direction.PRIOR));
	}

}
