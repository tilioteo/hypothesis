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
public class NextBranchEvent extends AbstractRunningEvent {

	public NextBranchEvent(Branch branch) {
		super(branch);
	}

	public Branch getBranch() {
		return (Branch) getSource();
	}

	public String getName() {
		return ProcessEventTypes.NextBranch;
	}

}
