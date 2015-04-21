/**
 * 
 */
package com.tilioteo.hypothesis.intercom;

import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
public interface HasUIMap {

	public UIMap getUIMap();
	
	public static class Getter {
		
		public static UIMap getCurrentUIMap() {
			VaadinServlet servlet = VaadinServlet.getCurrent();
			if (servlet instanceof HasUIMap) {
				return ((HasUIMap)servlet).getUIMap();
			}
			return null;
		}
	}
	
	public static class Broadcaster {
		public static void post(Object event) {
			UIMap uiMap = Getter.getCurrentUIMap();
			if (uiMap != null) {
				UI current = UI.getCurrent();
				for (UI ui : uiMap.keySet()) {
					if (current != ui && ui instanceof HasEventBus) {
						((HasEventBus)ui).post(event);
					}
				}
			}
		}
	}
}
