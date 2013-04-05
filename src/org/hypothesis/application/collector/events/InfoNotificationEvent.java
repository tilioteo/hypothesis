/**
 * 
 */
package org.hypothesis.application.collector.events;

import com.vaadin.ui.Window.Notification;

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

	public String getName() {
		return ProcessEvents.Null;
	}

	@Override
	public Notification getNotification() {
		return new Notification(caption, description,
				Notification.TYPE_HUMANIZED_MESSAGE);
	}

}
