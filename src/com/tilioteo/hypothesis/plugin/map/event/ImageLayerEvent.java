/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.ImageLayer;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class ImageLayerEvent extends AbstractComponentEvent<ImageLayer> {

	protected ImageLayerEvent(ImageLayerData data, ErrorHandler errorHandler) {
		super(data, errorHandler);
	}

	public static class Click extends ImageLayerEvent {

		public Click(ImageLayerData data) {
			this(data, null);
		}

		public Click(ImageLayerData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return MapEventTypes.LayerClick;
		}

	}

	public static class Load extends ImageLayerEvent {

		public Load(ImageLayerData data) {
			this(data, null);
		}

		public Load(ImageLayerData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return MapEventTypes.LayerLoad;
		}

	}

}
