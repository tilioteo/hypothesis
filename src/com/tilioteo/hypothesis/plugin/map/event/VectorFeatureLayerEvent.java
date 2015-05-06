/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.VectorFeatureLayer;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
public abstract class VectorFeatureLayerEvent extends AbstractComponentEvent<VectorFeatureLayer> {

	protected VectorFeatureLayerEvent(VectorFeatureLayerData data, ErrorHandler errorHandler) {
		super(data, errorHandler);
	}

	public static class Click extends VectorFeatureLayerEvent {

		public Click(VectorFeatureLayerData data) {
			this(data, null);
		}

		public Click(VectorFeatureLayerData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return MapEventTypes.LayerClick;
		}

	}
}
