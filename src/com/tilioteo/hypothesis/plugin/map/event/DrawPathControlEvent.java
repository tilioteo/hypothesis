/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.DrawPathControl;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
public abstract class DrawPathControlEvent extends AbstractComponentEvent<DrawPathControl> {

	protected DrawPathControlEvent(DrawPathControlData componentData, ErrorHandler errorHandler) {
		super(componentData, errorHandler);
	}

	public static class DrawPath extends DrawPathControlEvent {
		
		public DrawPath(DrawPathControlData data) {
			this(data, null);
		}

		public DrawPath(DrawPathControlData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return MapEventTypes.DrawPath;
		}
	}

}
