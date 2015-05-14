/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.DrawPointControl;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class DrawPointControlEvent extends AbstractComponentEvent<DrawPointControl> {

	protected DrawPointControlEvent(DrawPointControlData componentData, ErrorHandler errorHandler) {
		super(componentData, errorHandler);
	}

	public static class DrawPoint extends DrawPointControlEvent {
		
		public DrawPoint(DrawPointControlData data) {
			this(data, null);
		}

		public DrawPoint(DrawPointControlData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return MapEventTypes.DrawPoint;
		}
	}

}
