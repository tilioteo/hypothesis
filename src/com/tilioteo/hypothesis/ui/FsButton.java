/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import com.tilioteo.hypothesis.shared.ui.fsbutton.FsButtonState;
import com.vaadin.ui.Button;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class FsButton extends Button {
	
	public FsButton() {
		super();
	}
	
	public FsButton(String caption) {
		super(caption);
	}
	
	public FsButton(String caption, ClickListener listener) {
		super(caption, listener);
	}
	
	@Override
	protected FsButtonState getState() {
		return (FsButtonState)super.getState();
	}

	public void setEnableFullscreen(boolean value) {
		getState().fullscreen = value;
	}
	
	public boolean getEnableFullscreen() {
		return getState().fullscreen;
	}
}
