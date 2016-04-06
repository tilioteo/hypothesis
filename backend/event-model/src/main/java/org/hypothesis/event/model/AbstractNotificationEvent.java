/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import org.hypothesis.event.interfaces.ProcessViewEvent;

import com.vaadin.ui.Notification;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractNotificationEvent implements ProcessViewEvent {

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
