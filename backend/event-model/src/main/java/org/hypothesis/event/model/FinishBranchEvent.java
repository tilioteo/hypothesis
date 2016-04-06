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
