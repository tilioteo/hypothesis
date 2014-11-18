/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author kamil
 *
 */
public class VPanHandler extends AbstractNavigateHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler {

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

	public VPanHandler() {
		super();
		setStyleName(CLASSNAME);
	}
	
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
			panning = false;
			
			int totalX = event.getClientX() - startX;
			int totalY = event.getClientY() - startY;
			
			startX = startY = lastX = lastY = 0;
			
			if (layout != null) {
				layout.onPanEnd(totalX, totalY);
			}
		}

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

}
