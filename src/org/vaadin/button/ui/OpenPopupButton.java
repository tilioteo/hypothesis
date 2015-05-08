/**
 * 
 */
package org.vaadin.button.ui;

import org.vaadin.button.shared.ui.openpopupbutton.OpenPopupButtonClientRpc;

import com.tilioteo.hypothesis.model.UrlConsumer;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class OpenPopupButton extends Button implements UrlConsumer {

	public OpenPopupButton() {
		super();
	}

	public OpenPopupButton(Resource icon) {
		super(icon);
	}

	public OpenPopupButton(String caption, ClickListener listener) {
		super(caption, listener);
	}

	public OpenPopupButton(String caption, Resource icon) {
		super(caption, icon);
	}

	public OpenPopupButton(String caption) {
		super(caption);
	}

	public void setUrl(String url) {
		getRpcProxy(OpenPopupButtonClientRpc.class).setWindowUrl(url);
	}
}