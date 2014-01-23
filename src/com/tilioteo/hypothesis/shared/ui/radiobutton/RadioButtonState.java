/**
 * 
 */
package com.tilioteo.hypothesis.shared.ui.radiobutton;

import com.vaadin.shared.AbstractFieldState;

/**
 * @author kamil
 * 
 */
@SuppressWarnings("serial")
public class RadioButtonState extends AbstractFieldState {
	{
		primaryStyleName = "v-radiobutton";
	}

	public enum LabelPosition {
		Left, Right, Top, Bottom
	}

	public int clickShortcutKeyCode = 0;

	public boolean checked = false;
	public LabelPosition labelPosition = LabelPosition.Right;
	public boolean labelVisible = true;
    /**
     * If caption should be rendered in HTML
     */
    public boolean htmlContentAllowed = false;
    public String iconAltText = "";

}
