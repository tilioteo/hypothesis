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
public class TrayNotificationEvent extends AbstractNotificationEvent {

	public TrayNotificationEvent(SimpleTest test, String caption) {
		super(test, caption);
	}

	public TrayNotificationEvent(SimpleTest test, String caption, String description) {
		super(test, caption, description);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}

	@Override
	public Notification getNotification() {
		return new Notification(caption, description,
				Type.TRAY_NOTIFICATION);
	}

}
