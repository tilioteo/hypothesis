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
public class InfoNotificationEvent extends AbstractNotificationEvent {

	public InfoNotificationEvent(SimpleTest test, String caption) {
		super(test, caption);
	}

	public InfoNotificationEvent(SimpleTest test, String caption, String description) {
		super(test, caption, description);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}

	@Override
	public Notification getNotification() {
		return new Notification(caption, description,
				Type.HUMANIZED_MESSAGE);
	}

}
