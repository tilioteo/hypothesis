/**
 * 
 */
package org.hypothesis.application.collector.events;

import org.hypothesis.entity.Branch;

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

	public String getName() {
		return ProcessEvents.FinishBranch;
	}

}
