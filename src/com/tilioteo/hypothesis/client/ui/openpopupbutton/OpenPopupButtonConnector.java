/**
 * 
 */
package com.tilioteo.hypothesis.client.ui.openpopupbutton;

import com.tilioteo.hypothesis.client.ui.VOpenPopupButton;
import com.tilioteo.hypothesis.shared.ui.openpopupbutton.OpenPopupButtonClientRpc;
import com.vaadin.client.ui.button.ButtonConnector;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.ui.OpenPopupButton.class)
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
