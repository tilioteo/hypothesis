/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class FinishSlideEvent extends AbstractRunningEvent {

	public enum Direction {
		NEXT, PRIOR
	}

	private Direction direction;

	public FinishSlideEvent(Direction direction) {
		this(direction, null);
	}

	public FinishSlideEvent(Direction direction, ErrorHandler errorHandler) {
		super(errorHandler);
		this.direction = direction;
	}

	@Override
	public String getName() {
		return ProcessEventTypes.FinishSlide;
	}

	public Direction getDirection() {
		return direction;
	}

}
