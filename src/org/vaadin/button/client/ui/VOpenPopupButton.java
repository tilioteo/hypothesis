/**
 * 
 */
package org.vaadin.button.client.ui;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.vaadin.client.ui.VButton;

/**
 * @author kamil
 *
 */
public class VOpenPopupButton extends VButton {

	private static JavaScriptObject window = null;
	private static JavaScriptObject interval = null;
	
	private String url;
	
	public VOpenPopupButton() {
		super();
		
	}
	
	private native static void initWindow(VOpenPopupButton button, String url) /*-{
		var win = @org.vaadin.button.client.ui.VOpenPopupButton::window;
		if (win == null || win.closed) {
			win = $wnd.open(!url ? 'about:blank' : url,'popupWindow-'+Math.random().toString(36).substr(2,5),'menubar=no,location=no,status=no');
			@org.vaadin.button.client.ui.VOpenPopupButton::window = win;
			var interval = $wnd.setInterval(function() {
				try {
					if (win == null || win.closed) {
						$wnd.clearInterval(interval);
						button.@org.vaadin.button.client.ui.VOpenPopupButton::winClosed()();
					}
				}
				catch (e) {
				}
			}, 1000);
			@org.vaadin.button.client.ui.VOpenPopupButton::interval = interval;
		}
	}-*/;

	private native static void setUrl(VOpenPopupButton button, String url) /*-{
		var interval = @org.vaadin.button.client.ui.VOpenPopupButton::interval;
		var win = @org.vaadin.button.client.ui.VOpenPopupButton::window;
		if (!!win && !win.closed) {
			if (!url) {
				if (interval) {
					$wnd.clearInterval(interval);
				}
				win.close();
				button.@org.vaadin.button.client.ui.VOpenPopupButton::winClosed()();
			} else {
				win.location.href=url;
			}
		}
	}-*/;

	@Override
	public void onClick(ClickEvent event) {
		super.onClick(event);
		
		initWindow(this, url);
	}
	
	public void setWindowUrl(String url) {
		setUrl(this, url);
	}
	
	private void winClosed() {
		fireEvent(new WindowClosedEvent(this));
	}

	public interface WindowClosedEventHandler extends EventHandler {
		void windowClosed(WindowClosedEvent event);
	}

	public static class WindowClosedEvent extends GwtEvent<WindowClosedEventHandler> {

		public static final Type<WindowClosedEventHandler> TYPE = new Type<WindowClosedEventHandler>();

		public WindowClosedEvent(VOpenPopupButton button) {
			setSource(button);
		}

		public VOpenPopupButton getButton() {
			return (VOpenPopupButton) getSource();
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

}
