/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.vaadin.ui.Notification;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public abstract class AbstractNotificationEvent {

	protected String caption;
	protected String description;

	protected AbstractNotificationEvent(String caption) {
		this(caption, null);
	}

	protected AbstractNotificationEvent(String caption, String description) {
		this.caption = caption;
		this.description = description;
	}
	
	public String getCaption() {
		return caption;
	}
	
	public String getDescription() {
		return description;
	}

	public abstract Notification getNotification();
}
