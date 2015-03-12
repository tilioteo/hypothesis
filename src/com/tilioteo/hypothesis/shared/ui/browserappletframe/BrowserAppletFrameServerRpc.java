/**
 * 
 */
package com.tilioteo.hypothesis.shared.ui.browserappletframe;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 *
 */
public interface BrowserAppletFrameServerRpc extends ServerRpc {

	void readyChecked(boolean readyState);
	
}
