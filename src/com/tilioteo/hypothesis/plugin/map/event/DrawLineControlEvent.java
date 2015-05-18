/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.DrawLineControl;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class DrawLineControlEvent extends AbstractComponentEvent<DrawLineControl> {

	protected DrawLineControlEvent(DrawLineControlData componentData, ErrorHandler errorHandler) {
		super(componentData, errorHandler);
	}

	public static class DrawLine extends DrawLineControlEvent {
		
		public DrawLine(DrawLineControlData data) {
			this(data, null);
		}

		public DrawLine(DrawLineControlData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return MapEventTypes.DrawLine;
		}
	}

}
