/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import java.util.HashMap;
import java.util.Set;

import org.hypothesis.event.data.ComponentData;

import com.vaadin.ui.Component;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ComponentEvent extends AbstractUserEvent implements org.hypothesis.interfaces.ComponentEvent {

	private final Component component;
	private ComponentData data;
	private final String typeName;

	private final HashMap<String, Object> properties = new HashMap<>();
	private final HashMap<String, Class<?>> classes = new HashMap<>();
	private final HashMap<String, String> elementPaths = new HashMap<>();

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

	public void setProperty(String name, Object value, String elementPath) {
		properties.put(name, value);
		if (elementPath != null) {
			elementPaths.put(name, elementPath);
		}
	}

	@Override
	public void setProperty(String name, Class<?> clazz, Object value, String elementPath) {
		setProperty(name, value, elementPath);
		
		if (clazz != null) {
			classes.put(name, clazz);
		}
	}

	@Override
	public void setProperty(String name, Object value) {
		setProperty(name, value, null);
	}

	@Override
	public void setProperty(String name, Class<?> clazz, Object value) {
		setProperty(name, clazz, value, null);
	}

	public Object getProperty(String name) {
		return properties.get(name);
	}
	
	public Class<?> getPropertyClass(String name) {
		return classes.get(name);
	}

	public String getPropertyElementPath(String name) {
		return elementPaths.get(name);
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
