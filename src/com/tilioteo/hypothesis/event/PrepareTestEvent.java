/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.User;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class PrepareTestEvent extends AbstractProcessEvent {

	private User user;
	private boolean production;

	public PrepareTestEvent(Pack pack, User user, boolean production) {
		super(pack);
		this.user = user;
		this.production = production;
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}

	public Pack getPack() {
		return (Pack) getSource();
	}

	public User getUser() {
		return user;
	}

	public boolean isProduction() {
		return production;
	}
}
