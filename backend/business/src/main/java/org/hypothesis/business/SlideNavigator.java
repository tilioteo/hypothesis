/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.io.Serializable;

import org.hypothesis.event.data.Message;
import org.hypothesis.event.model.FinishSlideEvent;
import org.hypothesis.event.model.FinishSlideEvent.Direction;
import org.hypothesis.presenter.SlideContainerPresenter;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SlideNavigator implements Serializable {

	// TODO inject
	private SlideContainerPresenter presenter;

	/**
	 * 
	 * @param presenter
	 *            slide container presenter bound to this slide navigator
	 */
	public SlideNavigator(SlideContainerPresenter presenter) {
		this.presenter = presenter;
	}

	/**
	 * API method - go to next slide with validation Moving to next slide is
	 * allowed only if validation pass
	 */
	public void next() {
		next(true);
	}

	/**
	 * API method - go to next slide
	 * 
	 * @param validate
	 *            if false then validation is not performed
	 */
	public void next(boolean validate) {
		if (!validate || presenter.isValidSlide()) {
			presenter.fireEvent(new FinishSlideEvent(Direction.NEXT));
		}
	}

	/**
	 * API method - go to prior slide
	 */
	public void prior() {
		presenter.fireEvent(new FinishSlideEvent(Direction.PRIOR));
	}

	/**
	 * API method - post message to other clients
	 * 
	 * @param object
	 *            message object
	 */
	public void postMessage(Object object) {
		if (object != null && object instanceof Message) {
			Message message = (Message) object;
			message.updateTimestamp();
			presenter.postMessage(message.toString());
		}
	}

}
