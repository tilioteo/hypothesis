/**
 * 
 */
package com.tilioteo.hypothesis.event.model;

import java.util.HashMap;
import java.util.Set;

import com.tilioteo.hypothesis.event.data.ComponentData;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class ComponentEvent extends AbstractUserEvent {

	private Component component;
	private ComponentData data;
	private String typeName;

	private HashMap<String, Object> properties = new HashMap<>();
	private HashMap<String, String> patterns = new HashMap<>();

	public ComponentEvent(Component component, String typeName, String eventName) {
		super(null/* errorHandler */);
		this.component = component;
		this.typeName = typeName;
		setName(eventName);
	}

	public final Component getComponent() {
		return component;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setProperty(String name, Object value, String serializedPattern) {
		properties.put(name, value);
		if (serializedPattern != null) {
			patterns.put(name, serializedPattern);
		}
	}

	public void setProperty(String name, Object value) {
		setProperty(name, value, null);
	}

	public Object getProperty(String name) {
		return properties.get(name);
	}

	public String getPropertyPattern(String name) {
		return patterns.get(name);
	}

	public Set<String> getPropertyNames() {
		return properties.keySet();
	}

	public ComponentData getData() {
		return data;
	}

	public void setData(ComponentData data) {
		this.data = data;
	}
}
