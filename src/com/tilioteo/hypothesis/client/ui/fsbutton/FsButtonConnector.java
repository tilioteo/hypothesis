/**
 * 
 */
package com.tilioteo.hypothesis.client.ui.fsbutton;

import com.tilioteo.hypothesis.client.ui.VFsButton;
import com.tilioteo.hypothesis.shared.ui.fsbutton.FsButtonState;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.button.ButtonConnector;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.ui.FsButton.class)
public class FsButtonConnector extends ButtonConnector {

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
		
		if (stateChangeEvent.hasPropertyChanged("fullscreen")) {
			getWidget().setFullscreen(getState().fullscreen);
		}
	}

	@Override
	public VFsButton getWidget() {
		return (VFsButton)super.getWidget();
	}

	@Override
	public FsButtonState getState() {
		return (FsButtonState)super.getState();
	}
	
	

}
