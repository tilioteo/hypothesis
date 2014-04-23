/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.SimpleTest;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class AfterPrepareTestEvent extends AbstractProcessEvent {

	
	public AfterPrepareTestEvent(SimpleTest source) {
		super(source);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}
	
	public SimpleTest getTest() {
		return (SimpleTest) super.getSource();
	}

}
