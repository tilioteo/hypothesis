/**
 * 
 */
package com.tilioteo.hypothesis.business;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

import com.tilioteo.hypothesis.event.data.ComponentData;
import com.tilioteo.hypothesis.event.model.ComponentEvent;
import com.tilioteo.hypothesis.interfaces.Action;
import com.tilioteo.hypothesis.presenter.SlideContainerPresenter;
import com.tilioteo.hypothesis.utility.ReflectionUtility;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * @author kamil
 *
 */
public class EventManager {

	public static interface Callback {
		public void initEvent(ComponentEvent componentEvent);
	}

	public static final Callback DEFAULT_CALLBACK = new Callback() {
		@Override
		public void initEvent(ComponentEvent componentEvent) {
		}
	};

	private SlideContainerPresenter presenter;

	public EventManager(SlideContainerPresenter presenter) {
		this.presenter = presenter;
	}

	public void handleEvent(Component component, String typeName, String eventName, Action action, Callback callback) {
		ComponentEvent event = new ComponentEvent(component, typeName, eventName);
		callback.initEvent(event);

		ComponentData data = createComponentData(event);
		event.setData(data);
		presenter.fireEvent(event);

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
			if (value != null) {
				properties.put(name, value.getClass());
			} else {
				properties.put(name, Object.class);
			}

			if (event.getPropertyPattern(name) != null) {
				structures.put(name, event.getPropertyPattern(name));
			}
		}

		try {
			Class<?> dataClass = ComponentDataPojoGenerator.generate(ComponentData.class.getName() + "$Generated",
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

			field = ReflectionUtility.getDeclaredField(data, "id");
			field.setAccessible(true);
			field.set(data, component instanceof AbstractComponent ? ((AbstractComponent) component).getData() : null);

			field = ReflectionUtility.getDeclaredField(data, "eventName");
			field.setAccessible(true);
			field.set(data, event.getName());

			field = ReflectionUtility.getDeclaredField(data, "timestamp");
			field.setAccessible(true);
			field.set(data, event.getTimestamp());

			field = ReflectionUtility.getDeclaredField(data, "clientTimestamp");
			field.setAccessible(true);
			field.set(data, event.getClientTimestamp());

			return data;
		} catch (Throwable e) {
		}

		return null;
	}
}
