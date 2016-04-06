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
public class FinishTaskEvent extends AbstractRunningEvent {

	public FinishTaskEvent() {
		this(null);
	}

	public FinishTaskEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.FinishTask;
	}

}
