/**
 * 
 */
package com.tilioteo.hypothesis.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.vaadin.client.ui.VBrowserFrame;

/**
 * @author kamil
 * 
 */
public class VBrowserAppletFrame extends VBrowserFrame {

	public void checkReady() {
		boolean readyState = false;
		if (iframe != null) {
			readyState = isReady(iframe);
		}
		fireEvent(new ReadyCheckEvent(readyState));
	}

	private static final native boolean isReady(Element element) /*-{
		return element.contentWindow.isReady();
	}-*/;
	
	public void startBrowser(String token) {
		startBrowser(iframe, token);
	}

	private static final native void startBrowser(Element element, String token) /*-{
		element.contentWindow.startBrowser(token);
	}-*/;

	public interface ReadyCheckEventHandler extends EventHandler {
		void readyChecked(ReadyCheckEvent event);
	}

	public static class ReadyCheckEvent extends
			GwtEvent<ReadyCheckEventHandler> {

		public static final Type<ReadyCheckEventHandler> TYPE = new Type<ReadyCheckEventHandler>();

		private boolean readyState;

		public ReadyCheckEvent(boolean readyState) {
			this.readyState = readyState;
		}

		@Override
		public Type<ReadyCheckEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(ReadyCheckEventHandler handler) {
			handler.readyChecked(this);
		}

		public boolean getReadyState() {
			return readyState;
		}
	}

	public void addReadyCheckEventHandler(ReadyCheckEventHandler handler) {
		addHandler(handler, ReadyCheckEvent.TYPE);
	}

	public void removeReadyCheckEventHandler(ReadyCheckEventHandler handler) {
		// TODO
	}

}
