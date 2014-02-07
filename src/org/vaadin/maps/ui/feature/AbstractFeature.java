/**
 * 
 */
package org.vaadin.maps.ui.feature;

import org.vaadin.maps.shared.ui.feature.AbstractFeatureState;

import com.vaadin.ui.AbstractComponent;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractFeature extends AbstractComponent implements Feature {

	@Override
	protected AbstractFeatureState getState() {
		return (AbstractFeatureState) super.getState();
	}

}
