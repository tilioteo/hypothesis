/**
 * 
 */
package org.vaadin.jre.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ui.VLink;

/**
 * @author kamil
 *
 */
public class VInstallLink extends VLink {

	public VInstallLink() {
		super();
	}

	@Override
	public void onClick(ClickEvent event) {
		if (enabled) {
			if (target == null) {
				target = "_self";
			}
			String features;
			switch (borderStyle) {
			case NONE:
				features = "menubar=no,location=no,status=no";
				break;
			case MINIMAL:
				features = "menubar=yes,location=no,status=no";
				break;
			default:
				features = "";
				break;
			}

			if (targetWidth > 0) {
				features += (features.length() > 0 ? "," : "") + "width="
						+ targetWidth;
			}
			if (targetHeight > 0) {
				features += (features.length() > 0 ? "," : "") + "height="
						+ targetHeight;
			}

			if (features.length() > 0) {
				// if 'special features' are set, use window.open(), unless
				// a modifier key is held (ctrl to open in new tab etc)
				Event e = DOM.eventGetCurrentEvent();
				if (!e.getCtrlKey() && !e.getAltKey() && !e.getShiftKey()
						&& !e.getMetaKey()) {
					open(this, src, target, features);
					e.preventDefault();
				}
			}
		}
	}
	
	private void winClosed() {
		fireEvent(new WindowClosedEvent(this));
	}

	public native static void open(VInstallLink link, String url, String name, String features) /*-{
		var win = $wnd.open(url, name, features);
		var interval = $wnd.setInterval(function() {
			try {
				if (win == null || win.closed) {
					$wnd.clearInterval(interval);
					link.@org.vaadin.jre.client.ui.VInstallLink::winClosed()();
				}
			}
			catch (e) {
			}
		}, 1000);
	}-*/;

	public interface WindowClosedEventHandler extends EventHandler {
		void windowClosed(WindowClosedEvent event);
	}

	public static class WindowClosedEvent extends GwtEvent<WindowClosedEventHandler> {

		public static final Type<WindowClosedEventHandler> TYPE = new Type<WindowClosedEventHandler>();

		public WindowClosedEvent(VInstallLink link) {
			setSource(link);
		}

		public VInstallLink getLink() {
			return (VInstallLink) getSource();
		}

		@Override
		public Type<WindowClosedEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(WindowClosedEventHandler handler) {
			handler.windowClosed(this);

		}

	}

	public void addWindowClosedEventHandler(WindowClosedEventHandler handler) {
		addHandler(handler, WindowClosedEvent.TYPE);
	}

	//public void removeWindowClosedEventHandler(WindowClosedEventHandler handler) {
	//	removeHandler(handler, WindowClosedEvent.TYPE);
	//}

}
