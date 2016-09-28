/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.eventbus;

import org.hypothesis.cdi.Main;
import org.hypothesis.event.interfaces.EventBus;
import org.hypothesis.event.interfaces.MainUIEvent;

import com.vaadin.cdi.UIScoped;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Main
@UIScoped
public class MainEventBus extends HypothesisEventBus<MainUIEvent> implements EventBus {

	@Override
	public synchronized void post(Object event) {
		if (event instanceof MainUIEvent) {
			super.post(event);
		}
	}

}
