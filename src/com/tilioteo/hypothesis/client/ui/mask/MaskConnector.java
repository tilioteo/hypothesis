/**
 * 
 */
package com.tilioteo.hypothesis.client.ui.mask;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.tilioteo.hypothesis.shared.ui.mask.MaskClientRpc;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.ui.Mask.class)
public class MaskConnector extends AbstractExtensionConnector {
	
	private Widget widget;
	private Element maskElement = null;
	String position = null;
	
	public MaskConnector() {
		registerRpc(MaskClientRpc.class, new MaskClientRpc() {
			
			@Override
			public void show() {
				mask();
			}
			
			@Override
			public void hide() {
				unmask();
			}
		});
	}

	@Override
	protected void extend(ServerConnector target) {
		widget = ((ComponentConnector) target).getWidget();
		position = widget.getElement().getStyle().getPosition();
	}
	
	protected void mask() {
		if (null == maskElement && widget != null) {
			Element parentElement = widget.getElement();
			maskElement = DOM.createDiv();
			parentElement.appendChild(maskElement);
			DOM.setStyleAttribute(maskElement, "position", "absolute");
			DOM.setStyleAttribute(maskElement, "zIndex", "1000000000");
			DOM.setStyleAttribute(maskElement, "top", "0px");
			DOM.setStyleAttribute(maskElement, "left", "0px");
			DOM.setStyleAttribute(maskElement, "width", "100%");
			DOM.setStyleAttribute(maskElement, "height", "100%");
			DOM.setStyleAttribute(maskElement, "backgroundColor", "#808080");
			
			if (null == position || position.isEmpty()) {
				widget.getElement().getStyle().setPosition(Style.Position.RELATIVE);
			}
		}
	}
	
	protected void unmask() {
		if (maskElement != null) {
			maskElement.removeFromParent();
			maskElement = null;
			
			if (null == position || position.isEmpty()) {
				widget.getElement().getStyle().clearPosition();
			} else {
				widget.getElement().getStyle().setPosition(Style.Position.valueOf(position));
			}
		}
	}

}
