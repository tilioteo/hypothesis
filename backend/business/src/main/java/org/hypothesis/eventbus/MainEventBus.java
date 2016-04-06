/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.eventbus;

import java.util.HashMap;

import org.hypothesis.event.interfaces.MainUIEvent;
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
public class MainEventBus extends HypothesisEventBus<MainUIEvent> {

	private static HashMap<HasMainEventBus, MainEventBus> map = new HashMap<>();

	public static final MainEventBus createInstance(HasMainEventBus hasMainEventBus) {
		MainEventBus eventBus = new MainEventBus();
		map.put(hasMainEventBus, eventBus);

		return eventBus;
	}

	public static final void destroyInstance(HasMainEventBus hasMainEventBus) {
		map.remove(hasMainEventBus);
	}

	public static final MainEventBus getCurrent() {
		UI ui = UI.getCurrent();
		return get(ui);
	}

	public static final MainEventBus get(UI ui) {
		if (ui instanceof HasUIPresenter) {
			UIPresenter presenter = ((HasUIPresenter) ui).getPresenter();

			if (presenter instanceof HasMainEventBus) {
				return map.get(presenter);
			}
		}

		return null;
	}

	protected MainEventBus() {
	}

}
