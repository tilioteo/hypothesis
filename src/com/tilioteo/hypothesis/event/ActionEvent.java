/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.processing.AbstractBaseAction;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ActionEvent extends AbstractRunningEvent {

	public ActionEvent(AbstractBaseAction source) {
		super(source);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Action;
	}
	
	public AbstractBaseAction getAction() {
		return (AbstractBaseAction) getSource();
	}
}
