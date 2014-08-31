/**
 * 
 */
package org.vaadin.maps.ui.control;

import org.vaadin.maps.shared.ui.Style;
import org.vaadin.maps.shared.ui.control.DrawPathControlState;
import org.vaadin.maps.ui.handler.PathHandler;
import org.vaadin.maps.ui.layer.VectorFeatureLayer;

import com.tilioteo.hypothesis.plugin.map.MapUtility;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class DrawPathControl extends DrawFeatureControl<PathHandler> {
	
	protected Style startPointStyle = null;
	protected Style lineStyle = null;
	protected Style vertexStyle = null;

	public DrawPathControl(VectorFeatureLayer layer) {
		super(layer);
		
		setStartPointStyle(Style.DEFAULT_DRAW_START_POINT);
		setLineStyle(Style.DEFAULT_DRAW_LINE);
	}

	@Override
	protected DrawPathControlState getState() {
		return (DrawPathControlState) super.getState();
	}
	
	public Style getStartPointStyle() {
		return startPointStyle;
	}
	
	public void setStartPointStyle(Style style) {
		this.startPointStyle = style;
		getState().startPointStyle = MapUtility.getStyleMap(style);
		markAsDirty();
	}
	
	public Style getLineStyle() {
		return lineStyle;
	}
	
	public void setLineStyle(Style style) {
		this.lineStyle = style;
		getState().lineStyle = MapUtility.getStyleMap(style);
		markAsDirty();
	}
	
	public Style getVertexStyle() {
		return vertexStyle;
	}
	
	public void setVertexStyle(Style style) {
		this.vertexStyle = style;
		getState().vertexStyle = MapUtility.getStyleMap(style);
		markAsDirty();
	}
	
}