/**
 * 
 */
package org.vaadin.maps.client.ui;

import org.vaadin.maps.shared.ui.Style;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public class VDrawFeatureControl extends AbstractControl {

	public static final String CLASSNAME = "v-drawfeaturecontrol";


	private VVectorFeatureLayer layer = null;
	private Style cursorStyle = null;
	
	public VDrawFeatureControl() {
		super();
		setStyleName(CLASSNAME);
	}

	@Override
	public void setWidget(Widget widget) {
		if (widget != null && widget instanceof AbstractDrawFeatureHandler) {
			setHandler((AbstractDrawFeatureHandler)widget);
			
			getHandler().setCursorStyle(cursorStyle);
		}
	}
	
	public VVectorFeatureLayer getLayer() {
		return layer;
	}

	public void setLayer(VVectorFeatureLayer layer) {
		if (this.layer == layer) {
			return;
		}
		
		if (this.layer != null) {
			// TODO unset layer listeners
		}
		
		this.layer = layer;
	}
	
	@Override
	public AbstractDrawFeatureHandler getHandler() {
		return (AbstractDrawFeatureHandler) handler;
	}
	
	public Style getCursorStyle() {
		return cursorStyle;
	}
	
	public void setCursorStyle(Style style) {
		cursorStyle = style;
		
		if (handler != null) {
			getHandler().setCursorStyle(cursorStyle);
		}
	}
	
}
