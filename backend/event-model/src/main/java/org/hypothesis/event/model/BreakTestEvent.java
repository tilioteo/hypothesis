/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import org.hypothesis.data.api.Status;

import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class BreakTestEvent extends AbstractTestEvent {

	public BreakTestEvent() {
		this(null);
	}

	public BreakTestEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.BreakTest;
	}

	@Override
	public Status getStatus() {
		return Status.BROKEN_BY_CLIENT;
	}

}
