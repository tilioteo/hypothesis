/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Slide;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PriorSlideEvent extends AbstractRunningEvent {

	public PriorSlideEvent(Slide slide) {
		this(slide, null);
	}

	public PriorSlideEvent(Slide slide, ErrorHandler errorHandler) {
		super(slide, errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.PriorSlide;
	}

	public Slide getSlide() {
		return (Slide) getSource();
	}

}
