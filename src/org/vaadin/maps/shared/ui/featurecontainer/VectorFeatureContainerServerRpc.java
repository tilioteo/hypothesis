/**
 * 
 */
package org.vaadin.maps.shared.ui.featurecontainer;

import com.vaadin.shared.MouseEventDetails;

/**
 * @author kamil
 *
 */
public interface VectorFeatureContainerServerRpc extends
		AbstractFeatureContainerServerRpc {

	public void click(MouseEventDetails mouseDetails);
	
}
