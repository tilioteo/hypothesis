/**
 * 
 */
package org.vaadin.maps.ui.handler;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.vaadin.maps.shared.ui.handler.PanHandlerRpc;
import org.vaadin.maps.shared.ui.handler.PanHandlerState;
import org.vaadin.maps.ui.LayerLayout;
import org.vaadin.maps.ui.control.Control;

import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PanHandler extends NavigateHandler {

	protected LayerLayout layout = null;
	
	private PanHandlerRpc rpc = new PanHandlerRpc() {
		@Override
		public void panStart(int x, int y) {
			fireEvent(new PanStartEvent(PanHandler.this, x, y));
		}
		
		@Override
		public void panEnd(int deltaX, int deltaY) {
			fireEvent(new PanEndEvent(PanHandler.this, deltaX, deltaY));
		}
	};
	
	public PanHandler(Control control) {
		super(control);
		
		registerRpc(rpc);
	}

	@Override
	public void setLayout(LayerLayout layout) {
		this.layout = layout;
		getState().layout = layout;
	}

	@Override
	protected PanHandlerState getState() {
		return (PanHandlerState) super.getState();
	}
	
	/**
	 * This event is thrown, when the panning started.
	 * 
	 */
	public class PanStartEvent extends Component.Event {
		
		private int x;
		private int y;
		
		public PanStartEvent(PanHandler source, int x, int y) {
			super(source);
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}
	
	/**
	 * Interface for listening for a {@link PanStartEvent} fired by a
	 * {@link PanHandler}.
	 * 
	 */
	public interface PanStartListener extends Serializable {

		public static final Method PAN_START_METHOD =
				ReflectTools.findMethod(PanStartListener.class, "panStart", PanStartEvent.class);

		/**
		 * Called when panning started.
		 * 
		 * @param event
		 *            An event containing information about pan.
		 */
		public void panStart(PanStartEvent event);

	}
	
	/**
	 * This event is thrown, when the panning ended.
	 * 
	 */
	public class PanEndEvent extends Component.Event {
		
		private int dX;
		private int dY;
		
		public PanEndEvent(PanHandler source, int deltaX, int deltaY) {
			super(source);
			this.dX = deltaX;
			this.dY = deltaY;
		}

		public int getDeltaX() {
			return dX;
		}

		public int getDeltaY() {
			return dY;
		}
	}
	
	/**
	 * Interface for listening for a {@link PanEndEvent} fired by a
	 * {@link PanHandler}.
	 * 
	 */
	public interface PanEndListener extends Serializable {

		public static final Method PAN_END_METHOD =
				ReflectTools.findMethod(PanEndListener.class, "panEnd", PanEndEvent.class);

		/**
		 * Called when panning ended.
		 * 
		 * @param event
		 *            An event containing information about pan.
		 */
		public void panEnd(PanEndEvent event);

	}
	
	/**
	 * Adds the pan start listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addPanStartListener(PanStartListener listener) {
		addListener(PanStartEvent.class, listener,
				PanStartListener.PAN_START_METHOD);
	}

	/**
	 * Removes the pan start listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removePanStartListener(PanStartListener listener) {
		removeListener(PanStartEvent.class, listener,
				PanStartListener.PAN_START_METHOD);
	}

	/**
	 * Adds the pan end listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addPanEndListener(PanEndListener listener) {
		addListener(PanEndEvent.class, listener,
				PanEndListener.PAN_END_METHOD);
	}

	/**
	 * Removes the end start listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removePanEndListener(PanEndListener listener) {
		removeListener(PanEndEvent.class, listener,
				PanEndListener.PAN_END_METHOD);
	}

}
