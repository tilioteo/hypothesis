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
public class CloseTestEvent extends AbstractTestEvent {

	public CloseTestEvent() {
		this(null);
	}

	public CloseTestEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}

	@Override
	public Status getStatus() {
		return null;
	}

}
