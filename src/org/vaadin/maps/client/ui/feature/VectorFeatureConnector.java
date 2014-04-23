/**
 * 
 */
package org.vaadin.maps.client.ui.feature;

import org.vaadin.maps.client.drawing.Utils;
import org.vaadin.maps.client.io.ParseException;
import org.vaadin.maps.client.ui.MapUtility;
import org.vaadin.maps.client.ui.VVectorFeature;
import org.vaadin.maps.shared.ui.feature.FeatureServerRpc;
import org.vaadin.maps.shared.ui.feature.VectorFeatureState;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.feature.VectorFeature.class)
public class VectorFeatureConnector extends AbstractComponentConnector {

	@Override
	protected void init() {
		super.init();
		
		getWidget().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MouseEventDetails mouseDetails = MouseEventDetailsBuilder.buildMouseEventDetails(event.getNativeEvent(), getWidget().getElement());
				getRpcProxy(FeatureServerRpc.class).click(mouseDetails);
			}
		});

		getWidget().addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				MouseEventDetails mouseDetails = MouseEventDetailsBuilder.buildMouseEventDetails(event.getNativeEvent(), getWidget().getElement());
				getRpcProxy(FeatureServerRpc.class).doubleClick(mouseDetails);
			}
		});
}
	
	@Override
	public VVectorFeature getWidget() {
		return (VVectorFeature) super.getWidget();
	}

	@Override
	public VectorFeatureState getState() {
		return (VectorFeatureState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
		
		if (stateChangeEvent.hasPropertyChanged("hidden")) {
			getWidget().setHidden(getState().hidden);
		}
		if (stateChangeEvent.hasPropertyChanged("wkb")) {
			try {
				getWidget().setGeometry(Utils.hexWKBToGeometry(getState().wkb));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (stateChangeEvent.hasPropertyChanged("centroidX") ||
				stateChangeEvent.hasPropertyChanged("centroidY")) {
			getWidget().setCentroid(getState().centroidX, getState().centroidY);
		}
		if (stateChangeEvent.hasPropertyChanged("style")) {
			getWidget().setStyle(MapUtility.getStyleFromMap(getState().style));
		}
		if (stateChangeEvent.hasPropertyChanged("offsetX") ||
				stateChangeEvent.hasPropertyChanged("offsetY")) {
			getWidget().setTextOffset(getState().offsetX, getState().offsetY);
		}
		if (stateChangeEvent.hasPropertyChanged("text")) {
			getWidget().setText(getState().text);
		}
	}

}
