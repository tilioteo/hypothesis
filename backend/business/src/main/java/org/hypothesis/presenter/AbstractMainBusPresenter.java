package org.hypothesis.presenter;

import org.hypothesis.eventbus.HasMainEventBus;

@SuppressWarnings("serial")
public abstract class AbstractMainBusPresenter extends AbstractViewPresenter implements HasMainEventBus {

    @Override
    public void attach() {
        getBus().register(this);
    }

    @Override
    public void detach() {
        getBus().unregister(this);
    }

}
