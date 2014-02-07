/**
 * 
 */
package org.vaadin.maps.ui.controlcontainer;

import java.util.Iterator;
import java.util.LinkedList;

import org.vaadin.maps.ui.AbstractComponentContainer;
import org.vaadin.maps.ui.control.Control;

import com.vaadin.ui.Component;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractControlContainer<C extends Control> extends AbstractComponentContainer<C> {

	protected LinkedList<C> components = new LinkedList<C>();
	
    /**
     * Add a control into this container.
     * 
     * @param control
     *            the control to be added.
     */
    @Override
    public void addComponent(C control) {
        // Add to components before calling super.addComponent
        // so that it is available to AttachListeners
        components.add(control);
        try {
            super.addComponent(control);
        } catch (IllegalArgumentException e) {
            components.remove(control);
            throw e;
        }
        markAsDirty();
    }

    /**
     * Removes the control from this container.
     * 
     * @param control
     *            the control to be removed.
     */
    @Override
    public void removeComponent(C control) {
        components.remove(control);
        super.removeComponent(control);
        markAsDirty();
    }

    /**
     * Gets the component container iterator for going trough all the components
     * in the container.
     * 
     * @return the Iterator of the components inside the container.
     */
    @SuppressWarnings("unchecked")
	@Override
    public Iterator<Component> iterator() {
        return (Iterator<Component>) components.iterator();
    }

    @Override
	public Iterator<C> typedIterator() {
        return components.iterator();
	}

	/**
     * Gets the number of contained components. Consistent with the iterator
     * returned by {@link #iterator()}.
     * 
     * @return the number of contained components
     */
    @Override
    public int getComponentCount() {
        return components.size();
    }


	@Override
	public void replaceComponent(C oldControl, C newControl) {
		if (!components.contains(oldControl)) {
			addComponent(newControl);
		} else if (!components.contains(newControl)) {
			removeComponent(oldControl);
			addComponent(newControl);
		}

		markAsDirty();
	}

}
