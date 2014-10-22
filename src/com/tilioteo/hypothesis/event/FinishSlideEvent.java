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
public class FinishSlideEvent extends AbstractRunningEvent {

	public enum Direction {NEXT, PRIOR};
	
	private Direction direction;
	
	public FinishSlideEvent(Slide slide, Direction direction) {
		this(slide, direction, null);
	}

	public FinishSlideEvent(Slide slide, Direction direction, ErrorHandler errorHandler) {
		super(slide, errorHandler);
		this.direction = direction;
	}

	@Override
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
