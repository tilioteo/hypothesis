/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.DrawPolygonControl;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class DrawPolygonControlEvent extends AbstractComponentEvent<DrawPolygonControl> {

	protected DrawPolygonControlEvent(DrawPolygonControlData componentData, ErrorHandler errorHandler) {
		super(componentData, errorHandler);
	}

	public static class DrawPolygon extends DrawPolygonControlEvent {
		
		public DrawPolygon(DrawPolygonControlData data) {
			this(data, null);
		}

		public DrawPolygon(DrawPolygonControlData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return MapEventTypes.DrawPolygon;
		}
	}

}
