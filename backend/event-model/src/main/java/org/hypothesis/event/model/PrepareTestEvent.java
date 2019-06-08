/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import org.hypothesis.data.dto.TokenDto;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class PrepareTestEvent extends AbstractProcessEvent {

	private final TokenDto token;
	private final boolean startAllowed;

	public PrepareTestEvent(TokenDto token, boolean startAllowed) {
		super(null);
		this.token = token;
		this.startAllowed = startAllowed;
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}

	public TokenDto getToken() {
		return token;
	}

	public boolean isStartAllowed() {
		return startAllowed;
	}
}
