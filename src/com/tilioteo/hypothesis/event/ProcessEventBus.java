/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.event.HypothesisEvent.ProcessUIEvent;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ProcessEventBus extends HypothesisEventBus<ProcessUIEvent> {

	private static final ProcessEventBus instance = new ProcessEventBus();
	
	public static final ProcessEventBus get() {
		return instance;
	}
}
