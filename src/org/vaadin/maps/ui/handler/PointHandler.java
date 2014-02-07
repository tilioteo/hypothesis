/**
 * 
 */
package org.vaadin.maps.ui.handler;

import org.vaadin.maps.geometry.Utils;
import org.vaadin.maps.shared.ui.handler.PointHandlerServerRpc;
import org.vaadin.maps.shared.ui.handler.PointHandlerState;
import org.vaadin.maps.ui.control.Control;
import org.vaadin.maps.ui.feature.VectorFeature;
import org.vaadin.maps.ui.layer.VectorFeatureLayer;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public class PointHandler extends AbstractHandler implements RequiresVectorFeatureLayer {

	private PointHandlerServerRpc rpc = new PointHandlerServerRpc() {
		@Override
		public void geometry(String wkb) {
			addNewFeature(wkb);
		}
	};
	
	/**
	 * The last drawn feature
	 */
	protected VectorFeature feature = null;
	
	/**
	 * The drawing layer
	 */
	protected VectorFeatureLayer layer = null;
	
	public PointHandler(Control control) {
		super(control);
		registerRpc(rpc);
	}
	
	protected void addNewFeature(String wkb) {
		if (layer != null && wkb != null) {
			try {
				Geometry geometry = Utils.wkbHexToGeometry(wkb);

				if (geometry != null) {
					feature = new VectorFeature(geometry);
					layer.addComponent(feature);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected PointHandlerState getState() {
		return (PointHandlerState) super.getState();
	}

	@Override
	public void setLayer(VectorFeatureLayer layer) {
		this.layer = layer;
		getState().layer = layer;
	}
	
	@Override
	public boolean deactivate() {
		if (!super.deactivate())
			return false;
		
		cancel();
		
		return true;
	}
	
	@Override
	public void cancel() {
		super.cancel();
	}
	
}
                