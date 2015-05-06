/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.slide.ui.Window;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
public abstract class WindowEvent extends AbstractComponentEvent<Window> {
	
	public static class Init extends WindowEvent {

		public Init(WindowData data) {
			this(data, null);
		}

		public Init(WindowData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.WindowInit;
		}
		
	}

	public static class Open extends WindowEvent {

		public Open(WindowData data) {
			this(data, null);
		}

		public Open(WindowData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.WindowOpen;
		}
		
	}

	public static class Close extends WindowEvent {

		public Close(WindowData data) {
			this(data, null);
		}

		public Close(WindowData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.WindowClose;
		}
		
	}

	protected WindowEvent(WindowData data, ErrorHandler errorHandler) {
		super(data, errorHandler);
	}

}
