/**
 * 
 */
package org.hypothesis.event.model;

import org.hypothesis.event.model.FinishSlideEvent.Direction;

import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
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
