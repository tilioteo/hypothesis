/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
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
