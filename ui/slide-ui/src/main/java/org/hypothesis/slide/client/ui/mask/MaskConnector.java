/**
 * 
 */
package org.hypothesis.slide.client.ui.mask;

import org.hypothesis.slide.shared.ui.mask.MaskClientRpc;
import org.hypothesis.slide.shared.ui.mask.MaskState;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.hypothesis.slide.ui.Mask.class)
public class MaskConnector extends AbstractExtensionConnector {
	
	private Widget widget;
	private Element maskElement = null;
	Position position = null;
	
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
		position = positionFromString(widget.getElement().getStyle().getPosition());
	}
	
	protected void mask() {
		if (null == maskElement && widget != null) {
			Element parentElement = widget.getElement();
			maskElement = Document.get().createDivElement();
			parentElement.appendChild(maskElement);
			Style style = maskElement.getStyle();
			style.setPosition(Position.ABSOLUTE);
			style.setZIndex(1000000000);
			style.setTop(0, Unit.PX);
			style.setLeft(0, Unit.PX);
			style.setWidth(100, Unit.PCT);
			style.setHeight(100, Unit.PCT);
			
			style.setProperty("background", getState().color);
			//style.setOpacity(getState().opacity);
			
			if (null == position) {
				widget.getElement().getStyle().setPosition(Style.Position.RELATIVE);
			}
		}
	}
	
	private Position positionFromString(String position) {
		if (position != null && !position.trim().isEmpty()) {
			for (Position pos : Position.values()) {
				if (pos.getCssName().equalsIgnoreCase(position)) {
					return pos;
				}
			}
		}
		return null;
	}
	
	protected void unmask() {
		if (maskElement != null) {
			maskElement.removeFromParent();
			maskElement = null;
			
			if (null == position) {
				widget.getElement().getStyle().clearPosition();
			} else {
				widget.getElement().getStyle().setPosition(position);
			}
		}
	}
	
	@Override
	public MaskState getState() {
		return (MaskState)super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
		
		if (maskElement != null) {
			Style style = maskElement.getStyle();
			if (stateChangeEvent.hasPropertyChanged("color")) {
				style.setBackgroundColor(getState().color);
			}
			/*if (stateChangeEvent.hasPropertyChanged("opacity")) {
				style.setOpacity(getState().opacity);
			}*/
		}
	}
}
