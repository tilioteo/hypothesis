/**
 * 
 */
package com.tilioteo.hypothesis.slide.shared.ui.mask;

import com.vaadin.shared.communication.ClientRpc;

/**
 * @author kamil
 *
 */
public interface MaskClientRpc extends ClientRpc {
	
	public void show();
	public void hide();

}
