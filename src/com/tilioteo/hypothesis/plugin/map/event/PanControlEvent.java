/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.PanControl;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class PanControlEvent extends AbstractComponentEvent<PanControl> {

	protected PanControlEvent(PanControlData data, ErrorHandler errorHandler) {
		super(data, errorHandler);
	}
	
	public static class PanStart extends PanControlEvent {

		public PanStart(PanControlData data) {
			this(data, null);
		}
		
		public PanStart(PanControlData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return MapEventTypes.PanStart;
		}
		
	}

	public static class PanEnd extends PanControlEvent {

		public PanEnd(PanControlData data) {
			this(data, null);
		}
		
		public PanEnd(PanControlData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return MapEventTypes.PanEnd;
		}
		
	}

}
