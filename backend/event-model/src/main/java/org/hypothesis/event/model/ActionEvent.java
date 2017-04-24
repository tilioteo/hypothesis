/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import com.vaadin.server.ErrorHandler;
import org.hypothesis.interfaces.Action;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ActionEvent extends AbstractUserEvent {

	private final Action action;

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
