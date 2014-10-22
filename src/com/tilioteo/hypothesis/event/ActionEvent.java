/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ActionEvent extends AbstractRunningEvent {

	public ActionEvent(AbstractBaseAction source) {
		this(source, null);
	}

	public ActionEvent(AbstractBaseAction source, ErrorHandler errorHandler) {
		super(source, errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Action;
	}
	
	public AbstractBaseAction getAction() {
		return (AbstractBaseAction) getSource();
	}
}
