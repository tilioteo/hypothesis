/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.eventbus;

import java.util.HashMap;

import org.hypothesis.event.interfaces.ProcessEvent;
import org.hypothesis.interfaces.HasUIPresenter;
import org.hypothesis.interfaces.UIPresenter;
import org.hypothesis.presenter.ProcessUIPresenter;

import com.vaadin.ui.UI;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
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
