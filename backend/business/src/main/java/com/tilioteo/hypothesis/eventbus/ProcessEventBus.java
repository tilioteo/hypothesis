/**
 * 
 */
package com.tilioteo.hypothesis.eventbus;

import java.util.HashMap;

import com.tilioteo.hypothesis.event.interfaces.ProcessEvent;
import com.tilioteo.hypothesis.interfaces.HasUIPresenter;
import com.tilioteo.hypothesis.interfaces.UIPresenter;
import com.tilioteo.hypothesis.presenter.ProcessUIPresenter;
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ProcessEventBus extends HypothesisEventBus<ProcessEvent> {

	private static HashMap<ProcessUIPresenter, ProcessEventBus> map = new HashMap<>();

	public static final ProcessEventBus createInstance(ProcessUIPresenter presenter) {
		ProcessEventBus eventBus = new ProcessEventBus();
		map.put(presenter, eventBus);

		return eventBus;
	}

	public static final void destroyInstance(ProcessUIPresenter presenter) {
		map.remove(presenter);
	}

	public static final ProcessEventBus getCurrent() {
		UI ui = UI.getCurrent();
		return get(ui);
	}

	public static final ProcessEventBus get(UI ui) {
		if (ui instanceof HasUIPresenter) {
			UIPresenter presenter = ((HasUIPresenter) ui).getPresenter();

			if (presenter instanceof ProcessUIPresenter) {
				return map.get(presenter);
			}
		}

		return null;
	}

	protected ProcessEventBus() {
	}

}
