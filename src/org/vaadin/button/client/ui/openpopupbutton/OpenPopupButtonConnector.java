/**
 * 
 */
package org.vaadin.button.client.ui.openpopupbutton;

import org.vaadin.button.client.ui.VOpenPopupButton;
import org.vaadin.button.client.ui.VOpenPopupButton.WindowClosedEvent;
import org.vaadin.button.client.ui.VOpenPopupButton.WindowClosedEventHandler;
import org.vaadin.button.shared.ui.openpopupbutton.OpenPopupButtonClientRpc;
import org.vaadin.button.shared.ui.openpopupbutton.OpenPopupButtonServerRpc;

import com.vaadin.client.ui.button.ButtonConnector;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.button.ui.OpenPopupButton.class)
public class OpenPopupButtonConnector extends ButtonConnector implements WindowClosedEventHandler {

	@Override
	public void init() {
		super.init();
		
		getWidget().addWindowClosedEventHandler(this);

		registerRpc(OpenPopupButtonClientRpc.class, new OpenPopupButtonClientRpc() {
			@Override
			public void setWindowUrl(String url) {
				getWidget().setWindowUrl(url);
			}
		});
	}

	@Override
	public VOpenPopupButton getWidget() {
		return (VOpenPopupButton)super.getWidget();
	}

	@Override
	public void windowClosed(WindowClosedEvent event) {
		getRpcProxy(OpenPopupButtonServerRpc.class).windowClosed();
	}

}
