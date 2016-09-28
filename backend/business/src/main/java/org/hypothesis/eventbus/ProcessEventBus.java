/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.eventbus;

import org.hypothesis.cdi.Process;
import org.hypothesis.event.interfaces.EventBus;
import org.hypothesis.event.interfaces.ProcessEvent;

import com.vaadin.cdi.UIScoped;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Process
@UIScoped
public class ProcessEventBus extends HypothesisEventBus<ProcessEvent> implements EventBus {

	@Override
	public synchronized void post(Object event) {
		if (event instanceof ProcessEvent) {
			super.post(event);
		}
	}

}
