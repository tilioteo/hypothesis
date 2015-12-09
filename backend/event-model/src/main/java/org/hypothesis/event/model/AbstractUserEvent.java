/**
 * 
 */
package org.hypothesis.event.model;

import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class AbstractUserEvent extends AbstractProcessEvent {

	protected AbstractUserEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}

}
