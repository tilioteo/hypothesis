/**
 * 
 */
package org.vaadin.special.client.ui.selectbutton;

import org.vaadin.special.client.ui.VCheckBox;
import org.vaadin.special.shared.ui.selectbutton.CheckBoxState;

import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.special.ui.CheckBox.class)
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
