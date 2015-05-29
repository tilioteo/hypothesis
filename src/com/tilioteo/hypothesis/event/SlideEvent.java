/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class SlideEvent extends AbstractUserEvent {
	
	private SlideData slideData;
	
	public static class Init extends SlideEvent {

		public Init(SlideData data) {
			this(data, null);
		}
		
		public Init(SlideData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.SlideInit;
		}
	}
	
	public static class Show extends SlideEvent {

		public Show(SlideData data) {
			this(data, null);
		}

		public Show(SlideData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.SlideShow;
		}
	}
	
	public static class ShortcutKey extends SlideEvent {

		public ShortcutKey(SlideData data) {
			this(data, null);
		}
		
		public ShortcutKey(SlideData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.ShortcutKey;
		}
	}

	public static class Message extends SlideEvent {

		public Message(SlideData data) {
			this(data, null);
		}

		public Message(SlideData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.Message;
		}
	}
	
	protected SlideEvent(SlideData data, ErrorHandler errorHandler) {
		super(errorHandler);
		this.slideData = data;
	}

	public final SlideData getComponentData() {
		return slideData;
	}
}
