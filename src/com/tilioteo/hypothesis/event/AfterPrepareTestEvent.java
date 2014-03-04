/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Test;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class AfterPrepareTestEvent extends AbstractProcessEvent {

	
	public AfterPrepareTestEvent(Test source) {
		super(source);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}
	
	public Test getTest() {
		return (Test) super.getSource();
	}

}
