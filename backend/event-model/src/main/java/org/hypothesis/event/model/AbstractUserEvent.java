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
public class AbstractUserEvent extends AbstractProcessEvent {

	protected AbstractUserEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}

}
