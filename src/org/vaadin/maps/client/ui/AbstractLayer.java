/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author kamil
 *
 */
public class AbstractLayer extends SimplePanel implements Layer/*, Focusable*/ {
	
	public AbstractLayer() {
		super();
	}

    /*/**
     * Sets the keyboard focus on the layer
     * 
     * @param focus
     *            Should the layer have focus or not.
     */
    /*public void setFocus(boolean focus) {
        if (focus) {
            getContainerElement().focus();
        } else {
            getContainerElement().blur();
        }
    }

	@Override
	public void focus() {
        setFocus(true);
	}*/

}
