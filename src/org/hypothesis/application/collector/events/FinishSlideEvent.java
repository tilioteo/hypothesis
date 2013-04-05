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
public class FinishSlideEvent extends AbstractRunningEvent implements HasName {

	public FinishSlideEvent(Slide slide) {
		super(slide);
	}

	public String getName() {
		return ProcessEvents.FinishSlide;
	}

	public Slide getSlide() {
		return (Slide) getSource();
	}

}
