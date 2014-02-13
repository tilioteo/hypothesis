/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Slide;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PriorSlideEvent extends AbstractRunningEvent {

	public PriorSlideEvent(Slide slide) {
		super(slide);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.PriorSlide;
	}

	public Slide getSlide() {
		return (Slide) getSource();
	}

}
