/**
 * 
 */
package org.vaadin.button.client.ui.fullscreenbutton;

import org.vaadin.button.client.ui.VFullscreenButton;
import org.vaadin.button.shared.ui.fullscreenbutton.FullscreenButtonState;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.button.ButtonConnector;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.button.ui.FullscreenButton.class)
public class FullscreenButtonConnector extends ButtonConnector {

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
		
		if (stateChangeEvent.hasPropertyChanged("fullscreen")) {
			getWidget().setFullscreen(getState().fullscreen);
		}
	}

	@Override
	public VFullscreenButton getWidget() {
		return (VFullscreenButton)super.getWidget();
	}

	@Override
	public FullscreenButtonState getState() {
		return (FullscreenButtonState)super.getState();
	}
	
	

}
