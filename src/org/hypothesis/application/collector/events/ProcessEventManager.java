/**
 * 
 */
package org.hypothesis.application.collector.events;

import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;

import javax.swing.event.EventListenerList;

import com.vaadin.Application;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ProcessEventManager {

	private static HashMap<Application, ProcessEventManager> applicationEventManagers = new HashMap<Application, ProcessEventManager>();

	public static final ProcessEventManager get(Application application) {
		ProcessEventManager eventManager = applicationEventManagers
				.get(application);
		if (eventManager == null) {
			eventManager = new ProcessEventManager();
			registerApplicationEventManager(application, eventManager);
		}
		return eventManager;
	}

	public static final void registerApplicationEventManager(
			Application application, ProcessEventManager eventManager) {
		applicationEventManagers.put(application, eventManager);
	}

	public static final void unregisterApplicationEventManager(
			Application application) {
		applicationEventManagers.remove(application);
	}

	private HashMap<Class<? extends AbstractProcessEvent>, EventListenerList> listenersMap = new HashMap<Class<? extends AbstractProcessEvent>, EventListenerList>();

	private ProcessEventManager() {

	}

	public final void addListener(
			Class<? extends AbstractProcessEvent> eventClass,
			ProcessEventListener eventListener) {
		if (!isListening(eventListener, eventClass)) {
			EventListenerList listeners = listenersMap.get(eventClass);
			if (listeners == null) {
				listeners = new EventListenerList();
				listenersMap.put(eventClass, listeners);
			}
			listeners.add(ProcessEventListener.class, eventListener);
		}
	}

	@SafeVarargs
	public final void addListener(ProcessEventListener eventListener,
			Class<? extends AbstractProcessEvent>... eventClasses) {
		for (Class<? extends AbstractProcessEvent> eventClass : eventClasses) {
			addListener(eventClass, eventListener);
		}
	}

	@SuppressWarnings("unchecked")
	private EventListenerList findListeners(
			Class<? extends EventObject> eventClass) {
		EventListenerList listeners = listenersMap.get(eventClass);
		if (listeners == null
				&& !eventClass.getSuperclass().equals(EventObject.class))
			return findListeners((Class<? extends EventObject>) eventClass
					.getSuperclass());

		return listeners;
	}

	public final void fireEvent(AbstractProcessEvent event) {
		if (event != null) {
			EventListenerList listenerList = findListeners(event.getClass());

			if (listenerList != null) {
				Object[] listeners = listenerList.getListenerList();

				for (int i = 0; i < listeners.length; i += 2) {
					if (listeners[i] == ProcessEventListener.class) {
						((ProcessEventListener) listeners[i + 1])
								.handleEvent(event);
					}
				}
			}
		}
	}

	public final boolean isListening(ProcessEventListener eventListener,
			Class<? extends AbstractProcessEvent> eventClass) {
		EventListenerList listenerList = findListeners(eventClass);
		if (listenerList != null) {
			Object[] listeners = listenerList.getListenerList();

			for (int i = 0; i < listeners.length; i += 2) {
				if (listeners[i] == ProcessEventListener.class
						&& listeners[i + 1] == eventListener) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * public final void removeAllListeners(Class<? extends
	 * AbstractProcessEvent> eventClass) { EventListenerList listeners =
	 * listenersMap.get(eventClass); if (listeners != null) {
	 * listeners.remove(ProcessEventListener.class, eventListener); }
	 * listenersMap.remove(eventClass); }
	 * 
	 * public final void removeAllListeners() { listenersMap.clear(); }
	 */

	public final void removeListener(
			Class<? extends AbstractProcessEvent> eventClass,
			ProcessEventListener eventListener) {
		EventListenerList listeners = listenersMap.get(eventClass);
		if (listeners != null) {
			listeners.remove(ProcessEventListener.class, eventListener);
		}
	}

	public final void removeListener(ProcessEventListener eventListener) {
		Collection<EventListenerList> listenersCollection = listenersMap
				.values();
		for (EventListenerList listeners : listenersCollection) {
			if (listeners != null) {
				listeners.remove(ProcessEventListener.class, eventListener);
			}
		}
	}
}
