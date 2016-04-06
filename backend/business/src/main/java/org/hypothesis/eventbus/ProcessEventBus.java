/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.eventbus;

import java.util.HashMap;

import org.hypothesis.event.interfaces.ProcessEvent;
import org.hypothesis.interfaces.HasUIPresenter;
import org.hypothesis.interfaces.UIPresenter;

import com.vaadin.ui.UI;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ProcessEventBus extends HypothesisEventBus<ProcessEvent> {

	private static HashMap<HasProcessEventBus, ProcessEventBus> map = new HashMap<>();

	public static final ProcessEventBus createInstance(HasProcessEventBus hasProcessEventBus) {
		ProcessEventBus eventBus = new ProcessEventBus();
		map.put(hasProcessEventBus, eventBus);

		return eventBus;
	}

	public static final void destroyInstance(HasProcessEventBus hasProcessEventBus) {
		map.remove(hasProcessEventBus);
	}

	public static final ProcessEventBus getCurrent() {
		UI ui = UI.getCurrent();
		return get(ui);
	}

	public static final ProcessEventBus get(UI ui) {
		if (ui instanceof HasUIPresenter) {
			UIPresenter presenter = ((HasUIPresenter) ui).getPresenter();

			if (presenter instanceof HasProcessEventBus) {
				return map.get(presenter);
			}
		}

		return null;
	}

	protected ProcessEventBus() {
	}

}
