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
public class TrayNotificationEvent extends AbstractNotificationEvent {

	public TrayNotificationEvent(String caption) {
		super(caption);
	}

	public TrayNotificationEvent(String caption, String description) {
		super(caption, description);
	}

	public String getName() {
		return ProcessEvents.Null;
	}

	@Override
	public Notification getNotification() {
		return new Notification(caption, description,
				Notification.TYPE_TRAY_NOTIFICATION);
	}

}
