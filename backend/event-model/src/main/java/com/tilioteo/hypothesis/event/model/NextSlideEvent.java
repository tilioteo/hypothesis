/**
 * 
 */
package com.tilioteo.hypothesis.event.model;

import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class NextSlideEvent extends AbstractRunningEvent {

	public NextSlideEvent() {
		this(null);
	}

	public NextSlideEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.NextSlide;
	}

}
