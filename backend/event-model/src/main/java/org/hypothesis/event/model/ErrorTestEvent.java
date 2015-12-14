/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import org.hypothesis.data.model.Status;
import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ErrorTestEvent extends AbstractTestEvent {

	// TODO add reason
	// private String reason;

	public ErrorTestEvent() {
		this(null);
	}

	public ErrorTestEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.TestError;
	}

	@Override
	public Status getStatus() {
		return Status.BROKEN_BY_ERROR;
	}

}
