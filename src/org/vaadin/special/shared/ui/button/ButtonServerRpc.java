/**
 * 
 */
package org.vaadin.special.shared.ui.button;

import org.vaadin.special.shared.ClickRpc;

/**
 * @author kamil
 *
 */
public interface ButtonServerRpc extends ClickRpc {

	/**
     * Indicate to the server that the client has disabled the button as a
     * result of a click.
     */
    public void disableOnClick();
}
