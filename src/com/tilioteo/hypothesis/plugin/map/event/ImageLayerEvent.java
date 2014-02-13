/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.ImageLayer;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class ImageLayerEvent extends AbstractComponentEvent<ImageLayer> {

	protected ImageLayerEvent(ImageLayerData data) {
		super(data);
	}

	public static class Click extends ImageLayerEvent {

		public Click(ImageLayerData data) {
			super(data);
		}

		@Override
		public String getName() {
			return MapEventTypes.LayerClick;
		}

	}

	public static class Load extends ImageLayerEvent {

		public Load(ImageLayerData data) {
			super(data);
		}

		@Override
		public String getName() {
			return MapEventTypes.ImageLayerLoad;
		}

	}

}
