/**
 * 
 */
package org.vaadin.button.ui;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.vaadin.button.shared.ui.openpopupbutton.OpenPopupButtonClientRpc;
import org.vaadin.button.shared.ui.openpopupbutton.OpenPopupButtonServerRpc;

import com.tilioteo.hypothesis.model.UrlConsumer;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class OpenPopupButton extends Button implements UrlConsumer {
	
	private OpenPopupButtonServerRpc rpc = new OpenPopupButtonServerRpc() {
		@Override
		public void windowClosed() {
			fireEvent(new WindowClosedEvent(OpenPopupButton.this));
		}
	};

	public OpenPopupButton() {
		super();
		registerRpc(rpc);
	}

	public OpenPopupButton(Resource icon) {
		super(icon);
		registerRpc(rpc);
	}

	public OpenPopupButton(String caption, ClickListener listener) {
		super(caption, listener);
		registerRpc(rpc);
	}

	public OpenPopupButton(String caption, Resource icon) {
		super(caption, icon);
		registerRpc(rpc);
	}

	public OpenPopupButton(String caption) {
		super(caption);
		registerRpc(rpc);
	}

	public void setUrl(String url) {
		getRpcProxy(OpenPopupButtonClientRpc.class).setWindowUrl(url);
	}


	public static class WindowClosedEvent extends Component.Event {
		
		public static final String EVENT_ID = "windowClosed";

		public WindowClosedEvent(Component source) {
			super(source);
		}
	}
	
	public interface WindowClosedListener extends Serializable {
		
		public static final Method WINDOW_CLOSED_METHOD = ReflectTools
				.findMethod(WindowClosedListener.class, WindowClosedEvent.EVENT_ID, WindowClosedEvent.class);

		public void windowClosed(WindowClosedEvent event);
	}

	public void addWindowClosedListener(WindowClosedListener listener) {
		addListener(WindowClosedEvent.EVENT_ID, WindowClosedEvent.class, listener,
				WindowClosedListener.WINDOW_CLOSED_METHOD);
	}

	public void removeWindowClosedListener(WindowClosedListener listener) {
		removeListener(WindowClosedEvent.EVENT_ID, WindowClosedEvent.class, listener);
	}
	
}
