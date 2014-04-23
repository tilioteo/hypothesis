/**
 * 
 */
package com.tilioteo.hypothesis.shared.ui.browserappletframe;

import com.vaadin.shared.communication.ClientRpc;

/**
 * @author kamil
 *
 */
public interface BrowserAppletFrameClientRpc extends ClientRpc {

	public void startBrowser(String token);
	public void checkReadyState();

}
