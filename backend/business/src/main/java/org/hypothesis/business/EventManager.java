/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

import org.hypothesis.event.data.ComponentData;
import org.hypothesis.event.model.ComponentEvent;
import org.hypothesis.interfaces.Action;
import org.hypothesis.interfaces.ComponentEventCallback;
import org.hypothesis.presenter.SlideContainerPresenter;
import org.hypothesis.utility.ReflectionUtility;

import com.tilioteo.common.Strings;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class EventManager {

	// TODO may be injected
	private SlideContainerPresenter presenter;

	/**
	 * 
	 * @param presenter the slide container presenter which the event manager is associated with
	 */
	public EventManager(SlideContainerPresenter presenter) {
		this.presenter = presenter;
	}

	/**
	 * Event handling method which prepares ComponentEvent object with generated ComponentData from provided parameters
	 * @param component the component which is an originator and handles the event
	 * @param typeName name of component type, ie. "Button"
	 * @param eventName name of event, ie. "Click"
	 * @param action the action to execute
	 * @param callback used for user initialization of event
	 */
	public void handleEvent(Component component, String typeName, String eventName, Action action,
			ComponentEventCallback callback) {
		ComponentEvent event = new ComponentEvent(component, typeName, eventName);
		callback.initEvent(event);

		ComponentData data = createComponentData(event);
		event.setData(data);

		if (!(Strings.isNullOrEmpty(typeName) || Strings.isNullOrEmpty(eventName))) {
			presenter.fireEvent(event);
		}

		if (action != null) {
			presenter.setComponentData(data);
			action.execute();
			presenter.setComponentData(null);
		}
	}

	private ComponentData createComponentData(ComponentEvent event) {
		HashMap<String, Class<?>> properties = new HashMap<>();
		HashMap<String, String> structures = new HashMap<>();

		Set<String> names = event.getPropertyNames();
		for (String name : names) {
			Object value = event.getProperty(name);
			Class<?> clazz = event.getPropertyClass(name);
			if (clazz != null) {
				properties.put(name, clazz);
			} else if (value != null) {
				properties.put(name, value.getClass());
			} else {
				properties.put(name, Object.class);
			}

			if (event.getPropertyPattern(name) != null) {
				structures.put(name, event.getPropertyPattern(name));
			}
		}

		try {
			String safeClassName = event.getComponent().getClass().getName().replace(".", "_");
			Class<?> dataClass = ComponentDataPojoGenerator.generate(
					ComponentData.class.getName() + "$Generated" + "$" + safeClassName + "$" + event.getName(),
					ComponentData.class, properties, structures);

			ComponentData data = (ComponentData) dataClass.newInstance();

			for (String name : names) {
				Object value = event.getProperty(name);

				Field field = data.getClass().getDeclaredField(name);
				field.setAccessible(true);
				field.set(data, value);
			}

			Component component = event.getComponent();

			// set fixed fields
			Field field = ReflectionUtility.getDeclaredField(data, "sender");
			field.setAccessible(true);
			field.set(data, component);

			String str = component instanceof AbstractComponent && ((AbstractComponent) component).getData() != null
					? ((AbstractComponent) component).getData().toString() : null;
			field = ReflectionUtility.getDeclaredField(data, "id");
			field.setAccessible(true);
			field.set(data, str);

			field = ReflectionUtility.getDeclaredField(data, "eventName");
			field.setAccessible(true);
			field.set(data, event.getName());

			field = ReflectionUtility.getDeclaredField(data, "typeName");
			field.setAccessible(true);
			field.set(data, event.getTypeName());

			field = ReflectionUtility.getDeclaredField(data, "timestamp");
			field.setAccessible(true);
			field.set(data, event.getTimestamp());

			field = ReflectionUtility.getDeclaredField(data, "clientTimestamp");
			field.setAccessible(true);
			field.set(data, event.getClientTimestamp());

			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
