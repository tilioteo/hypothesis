/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Slide;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class NextSlideEvent extends AbstractRunningEvent {

	public NextSlideEvent(Slide slide) {
		super(slide);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.NextSlide;
	}

	public Slide getSlide() {
		return (Slide) getSource();
	}

}
