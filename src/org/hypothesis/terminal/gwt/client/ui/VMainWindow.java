/**
 * 
 */
package org.hypothesis.terminal.gwt.client.ui;

import com.google.gwt.core.client.JavaScriptObject;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.FocusableScrollPanel;
import com.vaadin.terminal.gwt.client.ui.VWindow;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class VMainWindow extends VWindow {

	public static native JavaScriptObject closeWindow(JavaScriptObject obj) /*-{
																			obj.close();
																			}-*/;

	public static native JavaScriptObject openWindow(String url, String name,
			String features) /*-{
								return $wnd.open(url, name, features);
								}-*/;

	private String id;

	private ApplicationConnection client;

	private final FocusableScrollPanel contentPanel = new FocusableScrollPanel();

	@Override
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		if (client.updateComponent(this, uidl, true)) {
			return;
		}

		super.updateFromUIDL(uidl, client);

		this.client = client;
		this.id = uidl.getId();

		int childIndex = 0;
		UIDL childUidl = uidl.getChildUIDL(childIndex++);
		while ("openautoclose".equals(childUidl.getTag())) {
			// TODO multiple opens with the same target will in practice just
			// open the last one - should we fix that somehow?
			final String parsedUri = client.translateVaadinUri(childUidl
					.getStringAttribute("src"));
			final String target;
			if (childUidl.hasAttribute("name"))
				target = childUidl.getStringAttribute("name");
			else
				target = "_blank";

			JavaScriptObject popup = openWindow(parsedUri, target, "");

			closeWindow(popup);

			childUidl = uidl.getChildUIDL(childIndex++);
		}
	}

}
