/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import org.hypothesis.interfaces.Action;

import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
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
