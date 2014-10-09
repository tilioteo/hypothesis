/**
 * 
 */
package com.tilioteo.hypothesis.shared.ui.mask;

import com.vaadin.shared.communication.ClientRpc;

/**
 * @author kamil
 *
 */
public interface MaskClientRpc extends ClientRpc {
	
	public void show();
	public void hide();

}
