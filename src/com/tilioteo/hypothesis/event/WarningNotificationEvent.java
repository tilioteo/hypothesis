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
public class WarningNotificationEvent extends AbstractNotificationEvent {

	public WarningNotificationEvent(String caption) {
		super(caption);
	}

	public WarningNotificationEvent(String caption, String description) {
		super(caption, description);
	}

	@Override
	public Notification getNotification() {
		return new Notification(caption, description, Type.WARNING_MESSAGE);
	}

}
