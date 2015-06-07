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
	
	public static final ProcessEventBus createInstance(ProcessUI ui) {
		ProcessEventBus processEventBus = new ProcessEventBus();
		map.put(ui, processEventBus);
		
		return processEventBus;
	}
	
	public static final ProcessEventBus get(UI ui) {
		if (ui instanceof ProcessUI) {
			return map.get((ProcessUI)ui);
		}
		
		return null;
	}
	
	protected ProcessEventBus() {
	}

}
