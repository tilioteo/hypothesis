/**
 * 
 */
package com.tilioteo.hypothesis.eventbus;

import java.util.HashMap;

import com.tilioteo.hypothesis.event.interfaces.MainUIEvent;
import com.tilioteo.hypothesis.interfaces.HasUIPresenter;
import com.tilioteo.hypothesis.interfaces.UIPresenter;
import com.tilioteo.hypothesis.presenter.MainUIPresenter;
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class MainEventBus extends HypothesisEventBus<MainUIEvent> {

	private static HashMap<MainUIPresenter, MainEventBus> map = new HashMap<>();

	public static final MainEventBus createInstance(MainUIPresenter presenter) {
		MainEventBus eventBus = new MainEventBus();
		map.put(presenter, eventBus);

		return eventBus;
	}

	public static final void destroyInstance(MainUIPresenter presenter) {
		map.remove(presenter);
	}

	public static final MainEventBus getCurrent() {
		UI ui = UI.getCurrent();
		return get(ui);
	}

	public static final MainEventBus get(UI ui) {
		if (ui instanceof HasUIPresenter) {
			UIPresenter presenter = ((HasUIPresenter) ui).getPresenter();

			if (presenter instanceof MainUIPresenter) {
				return map.get(presenter);
			}
		}

		return null;
	}

	protected MainEventBus() {
	}

}
