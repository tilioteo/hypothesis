/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Slide;
import com.tilioteo.hypothesis.event.FinishSlideEvent.Direction;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class AfterFinishSlideEvent extends AbstractRunningEvent {

	private Direction direction;
	
	public AfterFinishSlideEvent(Slide slide, Direction direction) {
		super(slide);
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
