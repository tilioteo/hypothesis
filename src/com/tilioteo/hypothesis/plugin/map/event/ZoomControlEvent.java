/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.ZoomControl;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
public abstract class ZoomControlEvent extends AbstractComponentEvent<ZoomControl> {

	protected ZoomControlEvent(ZoomControlData data, ErrorHandler errorHandler) {
		super(data, errorHandler);
	}
	
	public static class ZoomChange extends ZoomControlEvent {

		public ZoomChange(ZoomControlData data) {
			this(data, null);
		}
		
		public ZoomChange(ZoomControlData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return MapEventTypes.ZoomChange;
		}
		
	}

}
