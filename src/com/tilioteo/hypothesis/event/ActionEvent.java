/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.interfaces.Action;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
public class ActionEvent extends AbstractUserEvent {
	
	private Action action;

	public ActionEvent(Action action) {
		this(action, null);
	}

	public ActionEvent(Action action, ErrorHandler errorHandler) {
		super(errorHandler);
		this.action = action;
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Action;
	}
	
	public Action getAction() {
		return action;
	}
}
