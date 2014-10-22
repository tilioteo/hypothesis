/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Slide;
import com.tilioteo.hypothesis.event.FinishSlideEvent.Direction;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class AfterFinishSlideEvent extends AbstractRunningEvent {

	private Direction direction;
	
	public AfterFinishSlideEvent(Slide slide, Direction direction) {
		this(slide, direction, null);
	}

	public AfterFinishSlideEvent(Slide slide, Direction direction, ErrorHandler errorHandler) {
		super(slide, errorHandler);
		this.direction = direction;
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}

	public Slide getSlide() {
		return (Slide) getSource();
	}
	
	public Direction getDirection() {
		return direction;
	}

}
