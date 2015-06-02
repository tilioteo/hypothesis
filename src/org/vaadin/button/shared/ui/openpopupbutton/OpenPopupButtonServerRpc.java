/**
 * 
 */
package org.vaadin.button.shared.ui.openpopupbutton;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 *
 */
public interface OpenPopupButtonServerRpc extends ServerRpc {

	public void windowClosed();
}
