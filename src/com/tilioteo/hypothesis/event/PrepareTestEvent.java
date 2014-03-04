/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Token;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class PrepareTestEvent extends AbstractProcessEvent {

	private boolean startAllowed;

	public PrepareTestEvent(Token token, boolean startAllowed) {
		super(token);
		this.startAllowed = startAllowed;
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}

	public Token getToken() {
		return (Token) getSource();
	}

	public boolean isStartAllowed() {
		return startAllowed;
	}
}
