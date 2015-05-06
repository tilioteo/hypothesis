/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.event.FinishSlideEvent.Direction;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
public class AfterFinishSlideEvent extends AbstractRunningEvent {

	private Direction direction;
	
	public AfterFinishSlideEvent(Direction direction) {
		this(direction, null);
	}

	public AfterFinishSlideEvent(Direction direction, ErrorHandler errorHandler) {
		super(errorHandler);
		this.direction = direction;
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}

	public Direction getDirection() {
		return direction;
	}

}
