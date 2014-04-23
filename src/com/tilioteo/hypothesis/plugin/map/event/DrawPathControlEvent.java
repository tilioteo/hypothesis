/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.DrawPathControl;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class DrawPathControlEvent extends AbstractComponentEvent<DrawPathControl> {

	protected DrawPathControlEvent(AbstractComponentData<DrawPathControl> componentData) {
		super(componentData);
	}

	public static class DrawPath extends DrawPathControlEvent {
		
		public DrawPath(DrawPathControlData data) {
			super(data);
		}

		@Override
		public String getName() {
			return MapEventTypes.DrawPath;
		}
	}

}
