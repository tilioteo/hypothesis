/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Branch;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class FinishBranchEvent extends AbstractRunningEvent {

	public FinishBranchEvent(Branch branch) {
		super(branch);
	}

	public Branch getBranch() {
		return (Branch) getSource();
	}

	@Override
	public String getName() {
		return ProcessEventTypes.FinishBranch;
	}

}
