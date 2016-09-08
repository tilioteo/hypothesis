/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import org.hypothesis.event.model.EventQueue;
import org.hypothesis.eventbus.ProcessEventBus;
import org.hypothesis.presenter.SlideContainerPresenter;
import org.hypothesis.presenter.SlideContainerPresenterDeferred;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SlideContainerFactoryDeferred extends SlideContainerFactoryImpl {

	private final EventQueue queue = new EventQueue();
	private final ProcessEventBus bus;

	public SlideContainerFactoryDeferred(ProcessEventBus bus) {
		this.bus = bus;
	}

	@Override
	protected SlideContainerPresenter createSlideContainerPresenter() {

		return new SlideContainerPresenterDeferred(queue, bus);
	}

	public EventQueue getEventQueue() {
		return queue;
	}

}
