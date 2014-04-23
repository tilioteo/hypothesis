/**
 * 
 */
package org.vaadin.maps.shared.ui.feature;

import java.util.Map;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class VectorFeatureState extends AbstractFeatureState {

	public String wkb = null;
	public Double centroidX = null;
	public Double centroidY = null;
	public Map<String, String> style = null;
	public String text = null;
	public double offsetX = 0.0;
	public double offsetY = 0.0;
	public boolean hidden = false;
	
}
