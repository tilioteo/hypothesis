/**
 * 
 */
package com.tilioteo.hypothesis.shared.ui.selectbutton;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 *
 */
public interface SelectButtonServerRpc extends ServerRpc {

	public void setChecked(boolean checked, MouseEventDetails mouseEventDetails);

}
