/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import org.hypothesis.data.model.Token;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class PrepareTestEvent extends AbstractProcessEvent {

	private final Token token;
	private final boolean startAllowed;

	public PrepareTestEvent(Token token, boolean startAllowed) {
		super(null);
		this.token = token;
		this.startAllowed = startAllowed;
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}

	public Token getToken() {
		return token;
	}

	public boolean isStartAllowed() {
		return startAllowed;
	}
}
