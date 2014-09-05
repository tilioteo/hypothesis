/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.ImageSequenceLayer;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class ImageSequenceLayerEvent extends AbstractComponentEvent<ImageSequenceLayer> {

	protected ImageSequenceLayerEvent(ImageSequenceLayerData data) {
		super(data);
	}

	public static class Click extends ImageSequenceLayerEvent {

		public Click(ImageSequenceLayerData data) {
			super(data);
		}

		@Override
		public String getName() {
			return MapEventTypes.LayerClick;
		}

	}

	public static class Load extends ImageSequenceLayerEvent {

		public Load(ImageSequenceLayerData data) {
			super(data);
		}

		@Override
		public String getName() {
			return MapEventTypes.LayerLoad;
		}

	}

	public static class Change extends ImageSequenceLayerEvent {

		public Change(ImageSequenceLayerData data) {
			super(data);
		}

		@Override
		public String getName() {
			return MapEventTypes.ImageChange;
		}

	}

}
