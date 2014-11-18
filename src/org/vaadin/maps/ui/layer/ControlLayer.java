/**
 * 
 */
package org.vaadin.maps.ui.layer;

import java.util.Iterator;

import org.vaadin.maps.ui.control.AbstractControl;
import org.vaadin.maps.ui.controlcontainer.GeneralControlContainer;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ControlLayer extends AbstractLayer<GeneralControlContainer> {

	public ControlLayer() {
		GeneralControlContainer container = new GeneralControlContainer();
		setContent(container);
		setWidth("0px");
		setHeight("0px");
	}
	
	@Override
	public boolean isBase() {
		return false;
	}

	@Override
	public boolean isFixed() {
		return true;
	}

    /**
     * Add a control into this layer.
     * 
     * @param control
     *            the control to be added.
     */
    public void addComponent(AbstractControl control) {
    	getContent().addComponent(control);
    }

    /**
     * Adds the controls to this layer.
     * 
     * @param controls
     *            The controls to add.
     */
    public void addComponents(AbstractControl... controls) {
    	getContent().addComponents(controls);
    }

    /**
     * Removes the control from this layer.
     * 
     * @param controls
     *            the control to be removed.
     */
    public void removeComponent(AbstractControl controls) {
    	getContent().removeComponent(controls);
    }

    /**
     * Removes all controls from this layer.
     */
    public void removeAllComponents() {
    	getContent().removeAllComponents();
    }

    /**
     * Gets the typed control container iterator for going trough all the controls
     * in the layer.
     * 
     * @return the Iterator of the controls inside the layer.
     */
	public Iterator<AbstractControl> controlIterator() {
        return getContent().typedIterator();
	}

	/**
     * Gets the number of contained controls. Consistent with the iterator
     * returned by {@link #controlIterator()()}.
     * 
     * @return the number of contained controls
     */
    public int getFeatureCount() {
        return getContent().getComponentCount();
    }
    
    public void deactivateAllControls() {
    	getContent().deactivateAll();
    }

}
