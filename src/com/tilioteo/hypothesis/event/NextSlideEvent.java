/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Slide;
import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class NextSlideEvent extends AbstractRunningEvent {

	public NextSlideEvent(Slide slide) {
		this(slide, null);
	}

	public NextSlideEvent(Slide slide, ErrorHandler errorHandler) {
		super(slide, errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.NextSlide;
	}

	public Slide getSlide() {
		return (Slide) getSource();
	}

}
