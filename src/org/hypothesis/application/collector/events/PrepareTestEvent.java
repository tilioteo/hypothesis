/**
 * 
 */
package org.hypothesis.application.collector.events;

import org.hypothesis.entity.Pack;
import org.hypothesis.entity.User;

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

	public String getName() {
		return ProcessEvents.Null;
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
