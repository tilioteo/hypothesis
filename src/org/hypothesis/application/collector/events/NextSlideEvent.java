/**
 * 
 */
package org.hypothesis.application.collector.events;

import org.hypothesis.entity.Slide;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class NextSlideEvent extends AbstractRunningEvent implements HasName {

	public NextSlideEvent(Slide slide) {
		super(slide);
	}

	public String getName() {
		return ProcessEvents.NextSlide;
	}

	public Slide getSlide() {
		return (Slide) getSource();
	}

}
