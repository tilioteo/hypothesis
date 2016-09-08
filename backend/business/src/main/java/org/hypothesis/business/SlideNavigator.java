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

	private final SlideContainerPresenter presenter;

	public SlideNavigator(SlideContainerPresenter presenter) {
		this.presenter = presenter;
	}

	public void next() {
		next(true);
	}

	public void next(boolean validate) {
		if (!validate || presenter.isValidSlide()) {
			presenter.fireEvent(new FinishSlideEvent(Direction.NEXT));
		}
	}

	public void prior() {
		presenter.fireEvent(new FinishSlideEvent(Direction.PRIOR));
	}

	public void postMessage(Object object) {
		if (object != null && object instanceof Message) {
			Message message = (Message) object;
			message.updateTimestamp();
			presenter.postMessage(message.toString());
		}
	}

}
