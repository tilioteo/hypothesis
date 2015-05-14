/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractUserEvent extends AbstractProcessEvent {

	protected AbstractUserEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}

}
