/**
 * 
 */
package org.vaadin.special.shared.ui.selectbutton;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 *
 */
public interface SelectButtonServerRpc extends ServerRpc {

	public void setChecked(long timestamp, MouseEventDetails mouseEventDetails, boolean checked);

}
