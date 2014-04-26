/**
 * 
 */
package com.tilioteo.hypothesis.client.ui.selectbutton;

import com.tilioteo.hypothesis.client.ui.VRadioButton;
import com.tilioteo.hypothesis.shared.ui.selectbutton.RadioButtonState;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.ui.RadioButton.class)
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
