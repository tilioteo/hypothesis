/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.util.HashMap;

import com.tilioteo.hypothesis.event.HypothesisEvent.ProcessUIEvent;
import com.tilioteo.hypothesis.ui.ProcessUI;
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ProcessEventBus extends HypothesisEventBus<ProcessUIEvent> {

	private static HashMap<ProcessUI, ProcessEventBus> map = new HashMap<ProcessUI, ProcessEventBus>();
	
	public static final void createInstance(ProcessUI ui) {
		map.put(ui, new ProcessEventBus());
	}
	
	public static final ProcessEventBus get() {
		UI ui = UI.getCurrent();
		if (ui instanceof ProcessUI) {
			return map.get((ProcessUI)ui);
		}
		
		return null;
	}
	
	protected ProcessEventBus() {
	}

}
