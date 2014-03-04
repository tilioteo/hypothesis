/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Test;
import com.vaadin.ui.Notification;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractNotificationEvent extends AbstractProcessEvent {

	protected String caption;
	protected String description;

	protected AbstractNotificationEvent(Test test, String caption) {
		this(test, caption, null);
	}

	protected AbstractNotificationEvent(Test test, String caption, String description) {
		super(test);
		this.caption = caption;
		this.description = description;
	}
	
	public Test getTest() {
		return (Test)getSource();
	}
	
	public String getCaption() {
		return caption;
	}
	
	public String getDescription() {
		return description;
	}

	public abstract Notification getNotification();
}
