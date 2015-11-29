/**
 * 
 */
package com.tilioteo.hypothesis.event.model;

import com.tilioteo.hypothesis.data.model.Token;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class PrepareTestEvent extends AbstractProcessEvent {

	private Token token;
	private boolean startAllowed;

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
