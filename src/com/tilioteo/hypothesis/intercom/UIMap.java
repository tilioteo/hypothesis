/**
 * 
 */
package com.tilioteo.hypothesis.intercom;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.VaadinSession.State;
import com.vaadin.server.WrappedHttpSession;
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class UIMap extends HashMap<UI, UIWrapper> implements DetachListener {
	
	private HttpSessionSet httpSessions = new HttpSessionSet();
	
	public Set<HttpSession> getHttpSessions() {
		return httpSessions;
	}
	
	public Set<VaadinSession> getVaadinSessions() {
		HashSet<VaadinSession> sessions = new HashSet<VaadinSession>();
		
		for (HttpSession httpSession : httpSessions) {
			Collection<VaadinSession> vaadinSessions = VaadinSession.getAllSessions(httpSession);
			for (VaadinSession vaadinSession : vaadinSessions) {
				if (State.OPEN.equals(vaadinSession.getState())) {
					sessions.add(vaadinSession);
				}
			}
		}
		
		return sessions;
	}
	
	private void purge() {
		for (UIWrapper uiWrapper : super.values()) {
			if (!httpSessions.contains(uiWrapper.getHttpSession())) {
				remove(uiWrapper.getUi());
			}
		}
	}

	@Override
	public int size() {
		purge();
		return super.size();
	}

	@Override
	public boolean isEmpty() {
		purge();
		return super.isEmpty();
	}

	@Override
	public UIWrapper get(Object key) {
		purge();
		return super.get(key);
	}

	@Override
	public boolean containsKey(Object key) {
		purge();
		return super.containsKey(key);
	}

	@Override
	public Set<UI> keySet() {
		purge();
		return super.keySet();
	}

	@Override
	public Collection<UIWrapper> values() {
		purge();
		return super.values();
	}

	public void extractFromRequest(VaadinRequest request) {
		HttpSession currentHttpSession = ((WrappedHttpSession)request.getWrappedSession()).getHttpSession();
		httpSessions.add(currentHttpSession);
		
		for (HttpSession httpSession : httpSessions) {
			Collection<VaadinSession> vaadinSessions = VaadinSession.getAllSessions(httpSession);
			
			for (VaadinSession vaadinSession : vaadinSessions) {
				Collection<UI> uis = vaadinSession.getUIs();
			
				for (UI ui : uis) {
					if (!ui.isClosing() && ui.isAttached()) {
						UIWrapper uiWrapper = new UIWrapper(ui, httpSession, vaadinSession);
						if (!super.values().contains(uiWrapper)) {
							ui.addDetachListener(this);
							super.put(ui, uiWrapper);
						}
					}
				}
			}
		}
		
		// put the current UI
		UI currentUi = UI.getCurrent();
		currentUi.addDetachListener(this);
		put(currentUi, new UIWrapper(currentUi, currentHttpSession, VaadinSession.getCurrent()));
	}
	
	@SuppressWarnings("unchecked")
	public <T extends UI> Set<T> getUIs(Class<T> clazz) {
		HashSet<T> set = new HashSet<T>();
		
		for (UI ui : this.keySet()) {
			if (ui.getClass().equals(clazz)) {
				set.add((T)ui);
			}
		}
		return set;
	}
	
	public Set<UIWrapper> getUIWrappers(Class<?> clazz) {
		HashSet<UIWrapper> set = new HashSet<UIWrapper>();
		
		if (UI.class.isAssignableFrom(clazz)) {
			for (UIWrapper uiWrapper : this.values()) {
				if (uiWrapper.getUi().getClass().equals(clazz)) {
					set.add(uiWrapper);
				}
			}
		}
		return set;
	}

	@Override
	public void detach(DetachEvent event) {
		// detached ui needs to be removed
		remove(event.getSource());
	}
}
