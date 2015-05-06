/**
 * 
 */
package org.vaadin.button.client.ui.openpopupbutton;

import org.vaadin.button.client.ui.VOpenPopupButton;
import org.vaadin.button.shared.ui.openpopupbutton.OpenPopupButtonClientRpc;

import com.vaadin.client.ui.button.ButtonConnector;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.button.ui.OpenPopupButton.class)
public class OpenPopupButtonConnector extends ButtonConnector {


	@Override
	public void init() {
		super.init();

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

}
