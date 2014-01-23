/**
 * 
 */
package com.tilioteo.hypothesis.shared.ui.radiobutton;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 *
 */
public interface RadioButtonServerRpc extends ServerRpc {

	public void setChecked(boolean checked, MouseEventDetails mouseEventDetails);

}
