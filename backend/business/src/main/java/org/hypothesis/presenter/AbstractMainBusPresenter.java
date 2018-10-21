package org.hypothesis.presenter;

import org.hypothesis.eventbus.HasMainEventBus;
import org.hypothesis.eventbus.MainEventBus;

@SuppressWarnings("serial")
public abstract class AbstractMainBusPresenter extends AbstractViewPresenter implements HasMainEventBus {

	private MainEventBus bus = null;

	public MainEventBus getBus() {
		return bus;
	}

	public void setBus(MainEventBus bus) {
		if (this.bus != null) {
			this.bus.unregister(this);
		}

		this.bus = bus;
		if (this.bus != null) {
			this.bus.register(this);
		}
	}

	@Override
	public void attach() {
		if (bus != null) {
			bus.register(this);
		}
	}

	@Override
	public void detach() {
		if (bus != null) {
			bus.unregister(this);
		}
	}

}
