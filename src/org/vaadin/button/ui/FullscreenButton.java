/**
 * 
 */
package org.vaadin.button.ui;

import org.vaadin.button.shared.ui.fullscreenbutton.FullscreenButtonState;

import com.vaadin.ui.Button;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class FullscreenButton extends Button {
	
	public FullscreenButton() {
		super();
	}
	
	public FullscreenButton(String caption) {
		super(caption);
	}
	
	public FullscreenButton(String caption, ClickListener listener) {
		super(caption, listener);
	}
	
	@Override
	protected FullscreenButtonState getState() {
		return (FullscreenButtonState)super.getState();
	}

	public void setEnableFullscreen(boolean value) {
		getState().fullscreen = value;
	}
	
	public boolean getEnableFullscreen() {
		return getState().fullscreen;
	}
}
