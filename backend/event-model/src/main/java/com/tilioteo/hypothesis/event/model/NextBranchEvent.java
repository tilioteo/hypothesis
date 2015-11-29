/**
 * 
 */
package com.tilioteo.hypothesis.event.model;

import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class NextBranchEvent extends AbstractRunningEvent {

	public NextBranchEvent() {
		this(null);
	}

	public NextBranchEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.NextBranch;
	}

}
