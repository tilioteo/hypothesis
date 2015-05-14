/**
 * 
 */
package com.tilioteo.hypothesis.plugin.map.event;

import com.tilioteo.hypothesis.event.AbstractComponentEvent;
import com.tilioteo.hypothesis.plugin.map.ui.WMSLayer;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class WMSLayerEvent extends AbstractComponentEvent<WMSLayer> {

	protected WMSLayerEvent(WMSLayerData data, ErrorHandler errorHandler) {
		super(data, errorHandler);
	}

	public static class Click extends WMSLayerEvent {

		public Click(WMSLayerData data) {
			this(data, null);
		}

		public Click(WMSLayerData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return MapEventTypes.LayerClick;
		}

	}

	/*public static class Load extends ImageLayerEvent {

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

	}*/

}
