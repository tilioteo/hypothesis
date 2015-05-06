/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
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
