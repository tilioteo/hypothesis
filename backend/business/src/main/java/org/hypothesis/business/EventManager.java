/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import com.tilioteo.common.Strings;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import org.hypothesis.event.annotations.ElementPath;
import org.hypothesis.event.data.ComponentData;
import org.hypothesis.event.data.ComponentDataConstants;
import org.hypothesis.event.model.ComponentEvent;
import org.hypothesis.interfaces.Action;
import org.hypothesis.interfaces.ComponentEventCallback;
import org.hypothesis.presenter.SlideContainerPresenter;
import org.hypothesis.utility.ComponentDataUtility;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
	 * @param presenter
	 *            the slide container presenter which the event manager is
	 *            associated with
	 */
	public EventManager(SlideContainerPresenter presenter) {
		this.presenter = presenter;
	}

	/**
	 * Event handling method which prepares ComponentEvent object with generated
	 * ComponentData from provided parameters
	 * 
	 * @param component
	 *            the component which is an originator and handles the event
	 * @param typeName
	 *            name of component type, ie. "Button"
	 * @param eventName
	 *            name of event, ie. "Click"
	 * @param action
	 *            the action to execute
	 * @param callback
	 *            used for user initialization of event
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
		Map<String, Class<?>> properties = new HashMap<>();
		Map<String, Annotation> annotations = new HashMap<>();

		Set<String> names = event.getPropertyNames();
		names.forEach(e -> {
			Object value = event.getProperty(e);
			Class<?> clazz = event.getPropertyClass(e);
			if (clazz != null) {
				properties.put(e, clazz);
			} else if (value != null) {
				properties.put(e, value.getClass());
			} else {
				properties.put(e, Object.class);
			}

			if (event.getPropertyElementPath(e) != null) {
				annotations.put(e, createElementPathAnnotation(event.getPropertyElementPath(e)));
			}
		});

		try {
			String safeClassName = event.getComponent().getClass().getName().replace(".", "_");
			Class<?> dataClass = ComponentDataPojoGenerator.generate(
					ComponentData.class.getName() + "$Generated" + "$" + safeClassName + "$" + event.getName(),
					ComponentData.class, properties, annotations);

			ComponentData data = (ComponentData) dataClass.newInstance();

			names.forEach(e -> {
				Object value = event.getProperty(e);

				try {
					Field field = data.getClass().getDeclaredField(e);
					field.setAccessible(true);
					field.set(data, value);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});

			Component component = event.getComponent();

			// set fixed fields
			ComponentDataUtility.setComponentDataPropertyValue(data, ComponentDataConstants.PROP_SENDER, component);

			String str = component instanceof AbstractComponent && ((AbstractComponent) component).getData() != null
					? ((AbstractComponent) component).getData().toString() : null;

			ComponentDataUtility.setComponentDataPropertyValue(data, ComponentDataConstants.PROP_ID, str);
			ComponentDataUtility.setComponentDataPropertyValue(data, ComponentDataConstants.PROP_TYPE_NAME,
					event.getTypeName());
			ComponentDataUtility.setComponentDataPropertyValue(data, ComponentDataConstants.PROP_EVENT_NAME,
					event.getName());

			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private ElementPath createElementPathAnnotation(final String value) {
		return new ElementPath() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return ElementPath.class;
			}

			@Override
			public String value() {
				return value;
			}
		};
	}
}
