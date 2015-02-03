/**
 * 
 */
package org.vaadin.maps.ui.handler;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.vaadin.maps.shared.ui.handler.ZoomHandlerRpc;
import org.vaadin.maps.shared.ui.handler.ZoomHandlerState;
import org.vaadin.maps.ui.LayerLayout;
import org.vaadin.maps.ui.control.Control;

import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ZoomHandler extends NavigateHandler {

	protected LayerLayout layout = null;
	
	private ZoomHandlerRpc rpc = new ZoomHandlerRpc() {
		@Override
		public void zoomChange(double zoomStep) {
			fireEvent(new ZoomChangeEvent(ZoomHandler.this, zoomStep));
		}
	};
	
	public ZoomHandler(Control control) {
		super(control);
		
		registerRpc(rpc);
	}

	@Override
	public void setLayout(LayerLayout layout) {
		this.layout = layout;
		getState().layout = layout;
	}

	@Override
	protected ZoomHandlerState getState() {
		return (ZoomHandlerState) super.getState();
	}

	/**
	 * This event is thrown, when zoom changed.
	 * 
	 */
	public class ZoomChangeEvent extends Component.Event {
		
		private double zoomStep;
		
		public ZoomChangeEvent(ZoomHandler source, double zoomStep) {
			super(source);
			this.zoomStep = zoomStep;
		}

		public double getZoomStep() {
			return zoomStep;
		}

	}
	
	/**
	 * Interface for listening for a {@link ZoomChangeEvent} fired by a
	 * {@link ZoomHandler}.
	 * 
	 */
	public interface ZoomChangeListener extends Serializable {

		public static final Method ZOOM_CHANGE_METHOD =
				ReflectTools.findMethod(ZoomChangeListener.class, "zoomChange", ZoomChangeEvent.class);

		/**
		 * Called when zoom changed.
		 * 
		 * @param event
		 *            An event containing information about pan.
		 */
		public void zoomChange(ZoomChangeEvent event);

	}
	
	/**
	 * Adds the zoom change listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addZoomChangeListener(ZoomChangeListener listener) {
		addListener(ZoomChangeEvent.class, listener,
				ZoomChangeListener.ZOOM_CHANGE_METHOD);
	}

	/**
	 * Removes the zoom change listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeZoomChangeListener(ZoomChangeListener listener) {
		removeListener(ZoomChangeEvent.class, listener,
				ZoomChangeListener.ZOOM_CHANGE_METHOD);
	}

}
