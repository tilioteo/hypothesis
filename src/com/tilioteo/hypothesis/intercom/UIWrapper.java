/**
 * 
 */
package com.tilioteo.hypothesis.intercom;

import javax.servlet.http.HttpSession;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
public class UIWrapper {
	
	private UI ui;
	private HttpSession httpSession;
	private VaadinSession vaadinSession;
	
	public UIWrapper(UI ui, HttpSession httpSession, VaadinSession vaadinSession) {
		this.ui = ui;
		this.httpSession = httpSession;
		this.vaadinSession = vaadinSession;
	}

	public UI getUi() {
		return ui;
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	public VaadinSession getVaadinSession() {
		return vaadinSession;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((httpSession == null) ? 0 : httpSession.hashCode());
		result = prime * result + ((ui == null) ? 0 : ui.hashCode());
		result = prime * result
				+ ((vaadinSession == null) ? 0 : vaadinSession.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof UIWrapper)) {
			return false;
		}
		UIWrapper other = (UIWrapper) obj;
		if (httpSession == null) {
			if (other.httpSession != null) {
				return false;
			}
		} else if (httpSession != other.httpSession) {
			return false;
		}
		if (ui == null) {
			if (other.ui != null) {
				return false;
			}
		} else if (ui != other.ui) {
			return false;
		}
		if (vaadinSession == null) {
			if (other.vaadinSession != null) {
				return false;
			}
		} else if (vaadinSession != other.vaadinSession) {
			return false;
		}
		return true;
	}

}
