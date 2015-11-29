/**
 * 
 */
package com.tilioteo.hypothesis.event.model;

import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PriorSlideEvent extends AbstractRunningEvent {

	public PriorSlideEvent() {
		this(null);
	}

	public PriorSlideEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.PriorSlide;
	}

}
