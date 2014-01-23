/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.core.SlideManager;
import com.vaadin.ui.AbstractComponent;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public abstract class AbstractComponentData<T extends AbstractComponent> {

	private SlideManager slideManager;
	private T sender = null;

	protected AbstractComponentData(T sender, SlideManager slideManager) {
		this.sender = sender;
		this.slideManager = slideManager;
	}

	public final String getComponentId() {
		return sender != null ? (String) sender.getData() : null;
	}

	public T getSender() {
		return sender;
	}

	public final SlideManager getSlideManager() {
		return slideManager;
	}
}
