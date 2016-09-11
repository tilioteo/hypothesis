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

	private static final HashMap<HasProcessEventBus, ProcessEventBus> map = new HashMap<>();

	protected ProcessEventBus() {
	}

	/**
	 * create new event bus instance and register bus owner
	 * 
	 * @param hasProcessEventBus
	 * @return
	 */
	public static final ProcessEventBus createInstance(HasProcessEventBus hasProcessEventBus) {
		ProcessEventBus eventBus = new ProcessEventBus();
		map.put(hasProcessEventBus, eventBus);

		return eventBus;
	}

	/**
	 * unregister owner
	 * 
	 * @param hasProcessEventBus
	 */
	public static final void destroyInstance(HasProcessEventBus hasProcessEventBus) {
		map.remove(hasProcessEventBus);
	}

	public static final ProcessEventBus getCurrent() {
		UI ui = UI.getCurrent();
		return get(ui);
	}

	/**
	 * get event bus by ui
	 * 
	 * @param ui
	 * @return
	 */
	public static final ProcessEventBus get(UI ui) {
		if (ui instanceof HasUIPresenter) {
			UIPresenter presenter = ((HasUIPresenter) ui).getPresenter();

			if (presenter instanceof HasProcessEventBus) {
				return map.get(presenter);
			}
		}

		return null;
	}

}
