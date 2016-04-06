/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
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

	@Override
	public Notification getNotification() {
		return new Notification(caption, description, Type.TRAY_NOTIFICATION);
	}

}
