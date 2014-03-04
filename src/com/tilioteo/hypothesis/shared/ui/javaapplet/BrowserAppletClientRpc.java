/**
 * 
 */
package com.tilioteo.hypothesis.shared.ui.javaapplet;

import com.vaadin.shared.communication.ClientRpc;

/**
 * @author kamil
 *
 */
public interface BrowserAppletClientRpc extends ClientRpc {
	
	public void startBrowser(String token);

}
