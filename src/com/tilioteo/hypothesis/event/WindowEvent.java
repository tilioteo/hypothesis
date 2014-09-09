/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.ui.Window;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class WindowEvent extends AbstractComponentEvent<Window> {
	
	public static class Init extends WindowEvent {

		public Init(WindowData data) {
			super(data);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.WindowInit;
		}
		
	}

	public static class Open extends WindowEvent {

		public Open(WindowData data) {
			super(data);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.WindowOpen;
		}
		
	}

	public static class Close extends WindowEvent {

		public Close(WindowData data) {
			super(data);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.WindowClose;
		}
		
	}

	protected WindowEvent(WindowData data) {
		super(data);
	}

}
