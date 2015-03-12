/**
 * 
 */
package com.tilioteo.hypothesis.client.ui.selectbutton;

import com.tilioteo.hypothesis.client.ui.VCheckBox;
import com.tilioteo.hypothesis.shared.ui.selectbutton.CheckBoxState;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.ui.CheckBox.class)
public class CheckBoxConnector extends SelectButtonConnector {

	@Override
	public CheckBoxState getState() {
		return (CheckBoxState) super.getState();
	}

	@Override
	public VCheckBox getWidget() {
		return (VCheckBox) super.getWidget();
	}

}
