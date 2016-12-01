/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Focusable;
import org.hypothesis.event.data.Message;
import org.hypothesis.presenter.SlideContainerPresenter;

import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SlideDocument implements Serializable {

	// TODO inject
	private SlideContainerPresenter presenter;

	/**
	 * 
	 * @param presenter
	 *            parent slide container presenter associated with this document
	 */
	public SlideDocument(SlideContainerPresenter presenter) {
		this.presenter = presenter;
	}

	/**
	 * Look for defined component
	 * 
	 * @param id
	 *            component identifier
	 * @return requested component or null if not found
	 */
	public Component getComponentById(String id) {
		return presenter.getComponent(id);
	}

	/**
	 * Look for defined timer
	 * 
	 * @param id
	 *            timer identifier
	 * @return requested timer or null if not found
	 */
	public Component getTimerById(String id) {
		return presenter.getTimer(id);
	}

	/**
	 * Look for defined window
	 * 
	 * @param id
	 *            window identifier
	 * @return requested window or null if not found
	 */
	public Component getWindowById(String id) {
		return presenter.getWindow(id);
	}

	/**
	 * Create new message object from stored definition
	 * 
	 * @param uid
	 *            identifier of message
	 * @return new message object or null if uid not found or invalid definition
	 */
	public Message createMessage(String uid) {
		return presenter.createMessage(uid);
	}

	/**
	 * Remove focus from any component and focus to root if possible
	 */
	public void focus() {
		Component component = presenter.getSlideContainer();
		while (component != null && !(component instanceof Focusable)) {
			component = component.getParent();
		}

		if (component != null && component instanceof Focusable) {
			((Focusable) component).focus();
		}
	}
}
