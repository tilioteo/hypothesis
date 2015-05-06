/**
 * 
 */
package org.vaadin.button.client.ui;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.vaadin.client.ui.VButton;

/**
 * @author kamil
 *
 */
public class VOpenPopupButton extends VButton {

	private static JavaScriptObject window = null;
	
	private String url;
	
	public VOpenPopupButton() {
		super();
		
	}
	
	private native static void initWindow(String url) /*-{
		var win = @org.vaadin.button.client.ui.VOpenPopupButton::window;
		if (win == null || win.closed) {
			@org.vaadin.button.client.ui.VOpenPopupButton::window = $wnd.open(!url ? 'about:blank' : url,'popupWindow','menubar=no,location=no,status=no');
		}
	}-*/;

	private native static void setUrl(String url) /*-{
		var win = @org.vaadin.button.client.ui.VOpenPopupButton::window;
		if (!!win && !win.closed) {
			if (!url) {
				win.close();
			} else {
				win.location.href=url;
			}
		}
	}-*/;

	@Override
	public void onClick(ClickEvent event) {
		super.onClick(event);
		
		initWindow(url);
	}
	
	public void setWindowUrl(String url) {
		setUrl(url);
	}
	
}
