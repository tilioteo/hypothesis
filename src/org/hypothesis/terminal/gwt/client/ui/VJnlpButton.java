/**
 * 
 */
package org.hypothesis.terminal.gwt.client.ui;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.VButton;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class VJnlpButton extends VButton {
	public static final String CLASSNAME = "v-button";
	private static final String CLASSNAME_PRESSED = "v-pressed";

	// public static final String CLASSNAME = "v-jnlpbutton";
	// public static final String AJAX_EVENT_IDENTIFIER = "ajax";

	// private ApplicationConnection client;
	// private String id;

	public static native JavaScriptObject closeWindow(JavaScriptObject obj) /*-{
																			obj.close();
																			}-*/;

	public static native JavaScriptObject openWindow(String url, String name,
			String features) /*-{
								return $wnd.open(url, name, features);
								}-*/;

	private void openRequest(final String request) {
		if (request != null && request.length() > 0) {
			final JavaScriptObject jso = openWindow(
					request,
					"jnlp_window",
					"toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1,height=1");

			Timer timer = new Timer() {
				@Override
				public void run() {
					closeWindow(jso);
				}
			};
			timer.schedule(5000);
		}
	}

	@Override
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		if (client.updateComponent(this, uidl, true)) {
			return;
		}

		super.updateFromUIDL(uidl, client);

		// this.client = client;
		// this.id = uidl.getId();

		if (uidl.hasVariable("request")) {
			final String request = client.translateVaadinUri(uidl
					.getStringVariable("request"));
			openRequest(request);
		}
	}

}
