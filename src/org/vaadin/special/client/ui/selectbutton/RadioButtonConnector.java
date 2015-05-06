/**
 * 
 */
package org.vaadin.special.client.ui.selectbutton;

import org.vaadin.special.client.ui.VRadioButton;
import org.vaadin.special.shared.ui.selectbutton.RadioButtonState;

import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.special.ui.RadioButton.class)
public class RadioButtonConnector extends SelectButtonConnector {

	@Override
	public RadioButtonState getState() {
		return (RadioButtonState) super.getState();
	}

	@Override
	public VRadioButton getWidget() {
		return (VRadioButton) super.getWidget();
	}

}
