/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.util.HashMap;

import com.tilioteo.hypothesis.event.HypothesisEvent.MainUIEvent;
import com.tilioteo.hypothesis.ui.MainUI;
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class MainEventBus extends HypothesisEventBus<MainUIEvent> {
	
	private static HashMap<MainUI, MainEventBus> map = new HashMap<MainUI, MainEventBus>();
	
	public static final void createInstance(MainUI ui) {
		map.put(ui, new MainEventBus());
	}
	
	public static final MainEventBus get() {
		UI ui = UI.getCurrent();
		if (ui instanceof MainUI) {
			return map.get((MainUI)ui);
		}
		
		return null;
	}
	
	protected MainEventBus() {
	}

}
