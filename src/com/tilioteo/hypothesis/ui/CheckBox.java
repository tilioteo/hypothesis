/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import com.tilioteo.hypothesis.shared.ui.selectbutton.CheckBoxState;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class CheckBox extends SelectButton {

	public CheckBox() {
		super();
	}

	public CheckBox(String caption, boolean initialState) {
		super(caption, initialState);
	}

	public CheckBox(String caption, ClickListener listener) {
		super(caption, listener);
	}

	public CheckBox(String caption) {
		super(caption);
	}

	@Override
	protected CheckBoxState getState() {
		return (CheckBoxState) super.getState();
	}

}
