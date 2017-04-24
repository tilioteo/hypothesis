/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import com.vaadin.ui.Component;
import org.hypothesis.interfaces.Action;
import org.hypothesis.interfaces.ComponentEventCallback;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class EventWrapper {

	public final Component component;
	public final String typeName;
	public final String eventName;
	public final Action action;
	public final ComponentEventCallback callback;

	public EventWrapper(Component component, String typeName, String eventName, Action action,
			ComponentEventCallback callback) {
		this.component = component;
		this.typeName = typeName;
		this.eventName = eventName;
		this.action = action;
		this.callback = callback;
	}

}
