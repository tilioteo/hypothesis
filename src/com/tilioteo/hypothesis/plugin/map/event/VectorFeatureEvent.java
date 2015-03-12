/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.VectorFeature;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class VectorFeatureEvent extends AbstractComponentEvent<VectorFeature> {

	protected VectorFeatureEvent(VectorFeatureData data, ErrorHandler errorHandler) {
		super(data, errorHandler);
	}

	public static class Click extends VectorFeatureEvent {

		public Click(VectorFeatureData data) {
			this(data, null);
		}

		public Click(VectorFeatureData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return MapEventTypes.FeatureClick;
		}

	}
}
