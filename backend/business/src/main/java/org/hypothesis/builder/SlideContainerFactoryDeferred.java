/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import org.hypothesis.event.model.EventQueue;
import org.hypothesis.eventbus.HasProcessEventBus;
import org.hypothesis.presenter.SlideContainerPresenter;
import org.hypothesis.presenter.SlideContainerPresenterDeferred;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@SuppressWarnings("serial")
public class SlideContainerFactoryDeferred extends SlideContainerFactoryImpl implements HasProcessEventBus {

    private final EventQueue queue = new EventQueue();

    @Override
    protected SlideContainerPresenter createSlideContainerPresenter() {
        return new SlideContainerPresenterDeferred(queue);
    }

    public EventQueue getEventQueue() {
        return queue;
    }

}
