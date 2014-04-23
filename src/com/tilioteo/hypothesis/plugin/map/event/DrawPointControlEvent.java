/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentData;
import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.DrawPointControl;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class DrawPointControlEvent extends AbstractComponentEvent<DrawPointControl> {

	protected DrawPointControlEvent(AbstractComponentData<DrawPointControl> componentData) {
		super(componentData);
	}

	public static class DrawPoint extends DrawPointControlEvent {
		
		public DrawPoint(DrawPointControlData data) {
			super(data);
		}

		@Override
		public String getName() {
			return MapEventTypes.DrawPoint;
		}
	}

}
