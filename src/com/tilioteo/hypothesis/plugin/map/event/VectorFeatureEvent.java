/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.VectorFeature;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class VectorFeatureEvent extends AbstractComponentEvent<VectorFeature> {

	protected VectorFeatureEvent(VectorFeatureData data) {
		super(data);
	}

	public static class Click extends VectorFeatureEvent {

		public Click(VectorFeatureData data) {
			super(data);
		}

		@Override
		public String getName() {
			return MapEventTypes.FeatureClick;
		}

	}
}
