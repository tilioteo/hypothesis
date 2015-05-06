/**
 * 
 */
package org.vaadin.special.ui;

import org.vaadin.special.shared.ui.selectbutton.RadioButtonState;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class RadioButton extends SelectButton {

	public RadioButton() {
		super();
	}

	public RadioButton(String caption, boolean initialState) {
		super(caption, initialState);
	}

	public RadioButton(String caption, ClickListener listener) {
		super(caption, listener);
	}

	public RadioButton(String caption) {
		super(caption);
	}

	@Override
	protected RadioButtonState getState() {
		return (RadioButtonState) super.getState();
	}

}
