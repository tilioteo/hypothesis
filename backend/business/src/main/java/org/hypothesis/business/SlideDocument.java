/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.io.Serializable;

import org.hypothesis.event.data.Message;
import org.hypothesis.presenter.SlideContainerPresenter;

import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Focusable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SlideDocument implements Serializable {

	private final SlideContainerPresenter presenter;

	public SlideDocument(SlideContainerPresenter presenter) {
		this.presenter = presenter;
	}

	public Component getComponentById(String id) {
		return presenter.getComponent(id);
	}

	public Component getTimerById(String id) {
		return presenter.getTimer(id);
	}

	public Component getWindowById(String id) {
		return presenter.getWindow(id);
	}

	public Message createMessage(String uid) {
		return presenter.createMessage(uid);
	}
	
	public void focus() {
		Component component = presenter.getSlideContainer();
		while (component != null && !(component instanceof Focusable)) {
			component = component.getParent();
		}
		
		if (component != null && component instanceof Focusable) {
			((Focusable)component).focus();
		}
	}
}
