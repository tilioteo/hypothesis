/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Branch;
import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class NextBranchEvent extends AbstractRunningEvent {

	public NextBranchEvent(Branch branch) {
		this(branch, null);
	}

	public NextBranchEvent(Branch branch, ErrorHandler errorHandler) {
		super(branch, errorHandler);
	}

	public Branch getBranch() {
		return (Branch) getSource();
	}

	@Override
	public String getName() {
		return ProcessEventTypes.NextBranch;
	}

}
