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
public class FinishSlideEvent extends AbstractRunningEvent {

	public enum Direction {NEXT, PRIOR};
	
	private Direction direction;
	
	public FinishSlideEvent(Slide slide, Direction direction) {
		super(slide);
		this.direction = direction;
	}

	public String getName() {
		return ProcessEventTypes.FinishSlide;
	}

	public Slide getSlide() {
		return (Slide) getSource();
	}
	
	public Direction getDirection() {
		return direction;
	}

}
