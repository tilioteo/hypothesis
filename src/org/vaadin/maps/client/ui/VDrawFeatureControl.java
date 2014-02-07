/**
 * 
 */
package org.vaadin.maps.client.ui;

/**
 * @author kamil
 *
 */
public class VDrawFeatureControl extends AbstractControl {

	public static final String CLASSNAME = "v-drawfeaturecontrol";


	private VVectorFeatureLayer layer = null;
	
	public VDrawFeatureControl() {
		super();
		setStyleName(CLASSNAME);
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
	
}
