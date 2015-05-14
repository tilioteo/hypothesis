/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class FinishBranchEvent extends AbstractRunningEvent {

	public FinishBranchEvent() {
		this(null);
	}

	public FinishBranchEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.FinishBranch;
	}

}
