/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.VectorFeatureLayer;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class VectorFeatureLayerEvent extends AbstractComponentEvent<VectorFeatureLayer> {

	protected VectorFeatureLayerEvent(VectorFeatureLayerData data) {
		super(data);
	}

	public static class Click extends VectorFeatureLayerEvent {

		public Click(VectorFeatureLayerData data) {
			super(data);
		}

		@Override
		public String getName() {
			return MapEventTypes.LayerClick;
		}

	}
}
