/**
 * 
 */
package org.vaadin.maps.client.ui;

import java.util.HashMap;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author kamil
 *
 */
public class VPanHandler extends AbstractNavigateHandler implements HasLayerLayout, MouseDownHandler, MouseMoveHandler, MouseUpHandler {

	public static final String CLASSNAME = "v-panhandler";
	
	protected VLayerLayout layout = null;
	
	protected HandlerRegistration mouseDownHandler = null;
	protected HandlerRegistration mouseMoveHandler = null;
	protected HandlerRegistration mouseUpHandler = null;
	
	protected boolean panStarted = false;
	protected boolean panning = false;
	
	protected int startX;
	protected int startY;
	protected int lastX;
	protected int lastY;

	private HashMap<PanStartEventHandler, HandlerRegistration> panStartHandlerMap = new HashMap<PanStartEventHandler, HandlerRegistration>();
	private HashMap<PanEndEventHandler, HandlerRegistration> panEndHandlerMap = new HashMap<PanEndEventHandler, HandlerRegistration>();

	public VPanHandler() {
		super();
		setStyleName(CLASSNAME);
	}
	
	@Override
	public void setLayout(VLayerLayout layout) {
		if (this.layout == layout) {
			return;
		}
		
		finalize();
		this.layout = layout;
		initialize();
	}
	
	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (!active) {
			return;
		}
		
		if (event.getNativeButton() == NativeEvent.BUTTON_LEFT && !panStarted) {
			startX = event.getClientX();
			startY = event.getClientY();
			
			panStarted = true;
			
		}
		event.preventDefault();
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (event.getNativeButton() == NativeEvent.BUTTON_LEFT && panStarted) {
			lastX = event.getClientX();
			lastY = event.getClientY();
			
			int dX = lastX - startX;
			int dY = lastY - startY;
			
			if (Math.abs(dX) > 3 || Math.abs(dY) > 3) {
				if (!panning) {
					fireEvent(new PanStartEvent(this, startX, startY));
				}
				
				panning = true;
				
				if (layout != null) {
					layout.onPanStep(dX, dY);
				}
			}
		}
		
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (panStarted && panning) {
			
			int totalX = event.getClientX() - startX;
			int totalY = event.getClientY() - startY;
			
			fireEvent(new PanEndEvent(this, totalX, totalY));
			
			startX = startY = lastX = lastY = 0;
			
			if (layout != null) {
				layout.onPanEnd(totalX, totalY);
			}
		}

		panning = false;
		panStarted = false;
	}

	@Override
	protected void initialize() {
		if (layout != null) {
			ensureMouseHandlers();
		}
	}

	protected void ensureMouseHandlers() {
		mouseDownHandler = layout.addMouseDownHandler(this);
		mouseMoveHandler = layout.addMouseMoveHandler(this);
		mouseUpHandler = layout.addMouseUpHandler(this);
	}
	
	protected final void removeHandler(HandlerRegistration handler) {
		if (handler != null) {
			handler.removeHandler();
			handler = null;
		}
	}

	protected void removeMouseHandlers() {
		removeHandler(mouseDownHandler);
		removeHandler(mouseMoveHandler);
		removeHandler(mouseUpHandler);
	}
	
	@Override
	protected void finalize() {
		if (layout != null) {
			removeMouseHandlers();
		}
	}

	public interface PanStartEventHandler extends EventHandler {
		void panStart(PanStartEvent event);
	}

	public static class PanStartEvent extends GwtEvent<PanStartEventHandler> {

		public static final Type<PanStartEventHandler> TYPE = new Type<PanStartEventHandler>();
		
		private int x;
		private int y;
		
		public PanStartEvent(VPanHandler source, int x, int y) {
			setSource(source);
			this.x = x;
			this.y = y;
		}
		
		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		@Override
		public Type<PanStartEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(PanStartEventHandler handler) {
			handler.panStart(this);
		}
	}

	public interface PanEndEventHandler extends EventHandler {
		void panEnd(PanEndEvent event);
	}

	public static class PanEndEvent extends GwtEvent<PanEndEventHandler> {

		public static final Type<PanEndEventHandler> TYPE = new Type<PanEndEventHandler>();
		
		private int dx;
		private int dy;
		
		public PanEndEvent(VPanHandler source, int dx, int dy) {
			setSource(source);
			this.dx = dx;
			this.dy = dy;
		}
		
		public int getDeltaX() {
			return dx;
		}

		public int getDeltaY() {
			return dy;
		}

		@Override
		public Type<PanEndEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(PanEndEventHandler handler) {
			handler.panEnd(this);
		}
	}

	public void addPanStartEventHandler(PanStartEventHandler handler) {
		panStartHandlerMap.put(handler, addHandler(handler, PanStartEvent.TYPE));
	}

	public void removePanStartEventHandler(PanStartEventHandler handler) {
		if (panStartHandlerMap.containsKey(handler)) {
			removeEventHandler(panStartHandlerMap.get(handler));
			panStartHandlerMap.remove(handler);
		}
	}

	public void addPanEndEventHandler(PanEndEventHandler handler) {
		panEndHandlerMap.put(handler, addHandler(handler, PanEndEvent.TYPE));
	}

	public void removePanEndEventHandler(PanEndEventHandler handler) {
		if (panEndHandlerMap.containsKey(handler)) {
			removeEventHandler(panEndHandlerMap.get(handler));
			panEndHandlerMap.remove(handler);
		}
	}

}
