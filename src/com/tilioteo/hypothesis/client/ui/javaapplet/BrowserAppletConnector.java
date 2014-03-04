/**
 * 
 */
package com.tilioteo.hypothesis.client.ui.javaapplet;

import com.tilioteo.hypothesis.client.ui.VBrowserApplet;
import com.tilioteo.hypothesis.shared.ui.javaapplet.BrowserAppletClientRpc;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.ui.BrowserApplet.class)
public class BrowserAppletConnector extends JavaAppletConnector {
	
	@Override
	protected void init() {
		super.init();
		
		registerRpc(BrowserAppletClientRpc.class, new BrowserAppletClientRpc() {
			@Override
			public void startBrowser(String token) {
				getWidget().startBrowser(token);
			}
		});
	}

	@Override
	public VBrowserApplet getWidget() {
		return (VBrowserApplet) super.getWidget();
	}

}
