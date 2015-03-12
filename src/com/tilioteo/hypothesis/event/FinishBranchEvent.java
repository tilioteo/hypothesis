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
public class FinishBranchEvent extends AbstractRunningEvent {

	public FinishBranchEvent(Branch branch) {
		this(branch, null);
	}

	public FinishBranchEvent(Branch branch, ErrorHandler errorHandler) {
		super(branch, errorHandler);
	}

	public Branch getBranch() {
		return (Branch) getSource();
	}

	@Override
	public String getName() {
		return ProcessEventTypes.FinishBranch;
	}

}
