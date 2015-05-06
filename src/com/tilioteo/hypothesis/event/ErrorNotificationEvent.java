/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ErrorNotificationEvent extends AbstractNotificationEvent {

	public ErrorNotificationEvent(String caption) {
		super(caption);
	}

	public ErrorNotificationEvent(String caption, String description) {
		super(caption, description);
	}

	@Override
	public Notification getNotification() {
		return new Notification(caption, description, Type.ERROR_MESSAGE);
	}
}
