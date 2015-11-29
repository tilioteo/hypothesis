/**
 * 
 */
package com.tilioteo.hypothesis.business;

import java.io.Serializable;

import com.tilioteo.hypothesis.event.data.Message;
import com.tilioteo.hypothesis.presenter.SlideContainerPresenter;
import com.vaadin.ui.Component;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class SlideDocument implements Serializable {

	private SlideContainerPresenter presenter;

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
		return (Message) presenter.createMessage(uid);
	}
}
