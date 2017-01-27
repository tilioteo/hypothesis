/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder.impl;

import org.hypothesis.cdi.Deferred;
import org.hypothesis.event.model.EventQueue;
import org.hypothesis.presenter.SlideContainerPresenter;
import org.hypothesis.presenter.SlideContainerPresenterDeferred;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Deferred
public class SlideContainerFactoryDeferred extends SlideContainerFactoryImpl {

	private final EventQueue queue = new EventQueue();

	@Override
	protected SlideContainerPresenter createSlideContainerPresenter() {

		return new SlideContainerPresenterDeferred(procEvent, queue);
	}

	public EventQueue getEventQueue() {
		return queue;
	}

}
