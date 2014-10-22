/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.ImageSequenceLayer;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class ImageSequenceLayerEvent extends AbstractComponentEvent<ImageSequenceLayer> {

	protected ImageSequenceLayerEvent(ImageSequenceLayerData data, ErrorHandler errorHandler) {
		super(data, errorHandler);
	}

	public static class Click extends ImageSequenceLayerEvent {

		public Click(ImageSequenceLayerData data) {
			this(data, null);
		}

		public Click(ImageSequenceLayerData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return MapEventTypes.LayerClick;
		}

	}

	public static class Load extends ImageSequenceLayerEvent {

		public Load(ImageSequenceLayerData data) {
			this(data, null);
		}

		public Load(ImageSequenceLayerData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return MapEventTypes.LayerLoad;
		}

	}

	public static class Change extends ImageSequenceLayerEvent {

		public Change(ImageSequenceLayerData data) {
			this(data, null);
		}

		public Change(ImageSequenceLayerData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return MapEventTypes.ImageChange;
		}

	}

}
