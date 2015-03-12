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
public class WarningNotificationEvent extends AbstractNotificationEvent {

	public WarningNotificationEvent(SimpleTest test, String caption) {
		super(test, caption);
	}

	public WarningNotificationEvent(SimpleTest test, String caption, String description) {
		super(test, caption, description);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}

	@Override
	public Notification getNotification() {
		return new Notification(caption, description,
				Type.WARNING_MESSAGE);
	}

}
