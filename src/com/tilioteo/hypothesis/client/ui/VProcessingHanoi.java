/**
 * 
 */
package com.tilioteo.hypothesis.client.ui;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author kamil
 *
 */
public class VProcessingHanoi extends Composite {

	public static final String CLASSNAME = "VProcessing";

	private final Panel panel;

	protected Canvas canvas;

	public VProcessingHanoi() {

		panel = new SimplePanel();

		canvas = Canvas.createIfSupported();
		
		if (canvas != null) {
			setCanvasSize(300, 300);

			canvas.setStyleName("processing");
			canvas.sinkEvents(Event.MOUSEEVENTS + Event.ONMOUSEWHEEL + Event.KEYEVENTS);

			panel.add(canvas);
		}

		initWidget(panel);

		setStyleName(CLASSNAME);
		
		initProcessing();
	}

	public void setCanvasSize(int width, int height) {
		canvas.setWidth(width + "px");
		canvas.setHeight(height + "px");
	}

	private void initProcessing() {
		if (canvas != null) {
			initProcessingJs(canvas.getElement());
		}
	}

	native private static void initProcessingJs(Element canvas) /*-{
		
		function sketchProc(processing) {
			var t;
			
			processing.setup = function() {
				t = @com.tilioteo.hypothesis.client.Hanoi::new(Lcom/google/gwt/core/client/JavaScriptObject;)(this);
				t.@com.tilioteo.hypothesis.client.Hanoi::setup()();
			};
			
			processing.draw = function() {
				t.@com.tilioteo.hypothesis.client.Hanoi::draw()();
			};
		}

		try {
			var p = new $wnd.Processing(canvas, sketchProc);
		} catch(e) {
			alert("Failed to initialize processing object!\n\nError: " + e.message);
		}
	}-*/;

	@Override
	public void setHeight(String height) {
		if (canvas != null) {
			canvas.setHeight(height);
		} else {
			super.setHeight(height);
		}
	}

	@Override
	public void setWidth(String width) {
		if (canvas != null) {
			canvas.setWidth(width);
		} else {
			super.setWidth(width);
		}
	}


}
