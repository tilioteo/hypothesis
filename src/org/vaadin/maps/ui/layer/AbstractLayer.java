/**
 * 
 */
package org.vaadin.maps.ui.layer;

import org.vaadin.maps.shared.ui.AbstractLayerState;
import org.vaadin.maps.ui.AbstractSingleComponentContainer;

import com.vaadin.ui.Component;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractLayer<C extends Component> extends AbstractSingleComponentContainer<C> implements Layer {

    @Override
    protected AbstractLayerState getState() {
        return (AbstractLayerState) super.getState();
    }

}
