/**
 * 
 */
package com.tilioteo.hypothesis.plugin.processing.event;

import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.processing.ui.Processing;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class ProcessingEvent extends AbstractComponentEvent<Processing> {

	protected ProcessingEvent(ProcessingData componentData, ErrorHandler errorHandler) {
		super(componentData, errorHandler);
	}
	
	public static class Callback extends ProcessingEvent {

		public Callback(ProcessingData data) {
			this(data, null);
		}
		
		public Callback(ProcessingData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessingEventTypes.Callback;
		}
		
	}

}
