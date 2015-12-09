/**
 * 
 */
package org.hypothesis.event.model;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class InfoNotificationEvent extends AbstractNotificationEvent {

	public InfoNotificationEvent(String caption) {
		super(caption);
	}

	public InfoNotificationEvent(String caption, String description) {
		super(caption, description);
	}

	@Override
	public Notification getNotification() {
		return new Notification(caption, description, Type.HUMANIZED_MESSAGE);
	}

}
