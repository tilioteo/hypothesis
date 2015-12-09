/**
 * 
 */
package org.hypothesis.event.model;

import java.io.Serializable;
import java.util.EventObject;
import java.util.HashMap;

import javax.swing.event.EventListenerList;

import org.hypothesis.interfaces.ViewportEventListener;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class ViewportEventManager implements Serializable {

	private HashMap<Class<? extends ViewportEvent>, EventListenerList> listenersMap = new HashMap<Class<? extends ViewportEvent>, EventListenerList>();
	private boolean enabled = false;

	public final void addListener(Class<? extends ViewportEvent> eventClass, ViewportEventListener eventListener) {
		if (!isListening(eventListener, eventClass)) {
			EventListenerList listeners = listenersMap.get(eventClass);
			if (listeners == null) {
				listeners = new EventListenerList();
				listenersMap.put(eventClass, listeners);
			}
			listeners.add(ViewportEventListener.class, eventListener);
		}
	}

	@SafeVarargs
	public final void addListener(ViewportEventListener eventListener, Class<? extends ViewportEvent>... eventClasses) {
		for (Class<? extends ViewportEvent> eventClass : eventClasses) {
			addListener(eventClass, eventListener);
		}
	}

	@SuppressWarnings("unchecked")
	private EventListenerList findListeners(Class<? extends ViewportEvent> eventClass) {
		EventListenerList listeners = listenersMap.get(eventClass);
		if (listeners == null && !eventClass.getSuperclass().equals(EventObject.class))
			return findListeners((Class<? extends ViewportEvent>) eventClass.getSuperclass());

		return listeners;
	}

	public final void fireEvent(ViewportEvent event) {
		if (enabled && event != null) {
			EventListenerList listenerList = findListeners(event.getClass());

			if (listenerList != null) {
				Object[] listeners = listenerList.getListenerList();

				for (int i = 0; i < listeners.length; i += 2) {
					if (listeners[i] == ViewportEventListener.class) {
						((ViewportEventListener) listeners[i + 1]).handleEvent(event);
					}
				}
			}
		}
	}

	public final boolean isListening(ViewportEventListener eventListener, Class<? extends ViewportEvent> eventClass) {
		EventListenerList listenerList = findListeners(eventClass);
		if (listenerList != null) {
			Object[] listeners = listenerList.getListenerList();

			for (int i = 0; i < listeners.length; i += 2) {
				if (listeners[i] == ViewportEventListener.class && listeners[i + 1] == eventListener) {
					return true;
				}
			}
		}
		return false;
	}

	public final void removeAllListeners() {
		listenersMap.clear();
	}

	public final void removeAllListeners(Class<? extends ViewportEvent> eventClass) {
		EventListenerList listenerList = findListeners(eventClass);
		if (listenerList != null) {
			Object[] listeners = listenerList.getListenerList();

			for (int i = 0; i < listeners.length; i += 2) {
				if (listeners[i] == ViewportEventListener.class) {
					listenerList.remove(ViewportEventListener.class, (ViewportEventListener) listeners[i]);
				}
			}
		}
		listenersMap.remove(eventClass);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean disbled) {
		this.enabled = disbled;
	}

}
