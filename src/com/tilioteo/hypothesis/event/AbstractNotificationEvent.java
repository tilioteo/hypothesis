/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.io.Serializable;

import com.vaadin.ui.Notification;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractNotificationEvent implements Serializable {

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
