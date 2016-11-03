/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import com.vaadin.ui.Component;
import org.hypothesis.event.model.EventQueue;
import org.hypothesis.event.model.EventWrapper;
import org.hypothesis.event.model.FinishSlideEvent;
import org.hypothesis.interfaces.Action;
import org.hypothesis.interfaces.ComponentEventCallback;

import javax.enterprise.event.Observes;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SlideContainerPresenterDeferred extends SlideContainerPresenter {

	private final EventQueue eventQueue;
	private boolean disableDeferred = false;

	/**
	 * Construct
	 * 
	 * @param eventQueue
	 * @param bus
	 */
	public SlideContainerPresenterDeferred(EventQueue eventQueue) {
		super();

		this.eventQueue = eventQueue;
	}

	@Override
	public void handleEvent(Component component, String typeName, String eventName, Action action,
			ComponentEventCallback callback) {

		if (!disableDeferred) {
			if (eventQueue != null) {
				eventQueue.add(new EventWrapper(component, typeName, eventName, action, callback));
			}
		} else {
			getEventManager().handleEvent(component, typeName, eventName, action, callback);
		}
	}

	/**
	 * Fire all deferred events
	 */
	public void fireDeferred() {
		if (!disableDeferred) {
			if (eventQueue != null) {
				eventQueue.getList().forEach(
						e -> getEventManager().handleEvent(e.component, e.typeName, e.eventName, e.action, e.callback));

				eventQueue.clear();
			}

			disableDeferred = true;
		}
	}

	/**
	 * Do on finish slide
	 * 
	 * @param event
	 */
	public void processFinishSlide(@Observes FinishSlideEvent event) {
		viewDone();
	}
}
