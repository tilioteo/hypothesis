/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.event.HypothesisEvent.MainUIEvent;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class MainEventBus extends HypothesisEventBus<MainUIEvent> {
	
	private static final MainEventBus instance = new MainEventBus();
	
	public static final MainEventBus get() {
		return instance;
	}
}
