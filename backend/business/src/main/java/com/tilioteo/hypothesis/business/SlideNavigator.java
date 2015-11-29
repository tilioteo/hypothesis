/**
 * 
 */
package com.tilioteo.hypothesis.business;

import java.io.Serializable;

import com.tilioteo.hypothesis.event.data.Message;
import com.tilioteo.hypothesis.event.model.FinishSlideEvent;
import com.tilioteo.hypothesis.event.model.FinishSlideEvent.Direction;
import com.tilioteo.hypothesis.presenter.SlideContainerPresenter;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class SlideNavigator implements Serializable {

	private SlideContainerPresenter presenter;

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
