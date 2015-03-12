/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.SimpleTest;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class ErrorNotificationEvent extends AbstractNotificationEvent {

	public ErrorNotificationEvent(SimpleTest test, String caption) {
		super(test, caption);
	}

	public ErrorNotificationEvent(SimpleTest test, String caption, String description) {
		super(test, caption, description);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}

	@Override
	public Notification getNotification() {
		return new Notification(caption, description,
				Type.ERROR_MESSAGE);
	}
}
