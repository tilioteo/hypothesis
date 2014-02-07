/**
 * 
 */
package org.vaadin.maps.ui.featurecontainer;

import java.util.Iterator;
import java.util.LinkedList;

import org.vaadin.maps.ui.AbstractComponentContainer;
import org.vaadin.maps.ui.feature.Feature;

import com.vaadin.ui.Component;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractFeatureContainer<F extends Feature> extends AbstractComponentContainer<F> {

	protected LinkedList<F> components = new LinkedList<F>();
	
    /**
     * Add a feature into this container.
     * 
     * @param feature
     *            the feature to be added.
     */
    @Override
    public void addComponent(F feature) {
        // Add to components before calling super.addComponent
        // so that it is available to AttachListeners
        components.add(feature);
        try {
            super.addComponent(feature);
        } catch (IllegalArgumentException e) {
            components.remove(feature);
            throw e;
        }
        markAsDirty();
    }

    /**
     * Adds a feature into indexed position in this container.
     * 
     * @param feature
     *            the feature to be added.
     * @param index
     *            the index of the feature position. The features currently
     *            in and after the position are shifted forwards.
     */
    public void addComponent(F feature, int index) {
        // If feature is already in this, we must remove it before proceeding
        if (feature.getParent() == this) {
            // When feature is removed, all components after it are shifted down
            if (index > getComponentIndex(feature)) {
                index--;
            }
            removeComponent(feature);
        }
        components.add(index, feature);
        try {
            super.addComponent(feature);
        } catch (IllegalArgumentException e) {
            components.remove(feature);
            throw e;
        }
        markAsDirty();
    }

    /**
     * Removes the feature from this container.
     * 
     * @param feature
     *            the feature to be removed.
     */
    @Override
    public void removeComponent(F feature) {
        components.remove(feature);
        super.removeComponent(feature);
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
	public Iterator<F> typedIterator() {
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

    /* Documented in superclass */
    @Override
    public void replaceComponent(F oldFeature, F newFeature) {

        // Gets the locations
        int oldLocation = -1;
        int newLocation = -1;
        int location = 0;
        for (final Iterator<F> i = components.iterator(); i.hasNext();) {
            final F feature = i.next();

            if (feature == oldFeature) {
                oldLocation = location;
            }
            if (feature == newFeature) {
                newLocation = location;
            }

            location++;
        }

        if (oldLocation == -1) {
            addComponent(newFeature);
        } else if (newLocation == -1) {
            removeComponent(oldFeature);
            addComponent(newFeature, oldLocation);
        } else {
            // Both old and new are in the container
            if (oldLocation > newLocation) {
                components.remove(oldFeature);
                components.add(newLocation, oldFeature);
                components.remove(newFeature);
                components.add(oldLocation, newFeature);
            } else {
                components.remove(newFeature);
                components.add(oldLocation, newFeature);
                components.remove(oldFeature);
                components.add(newLocation, oldFeature);
            }

            markAsDirty();
        }
    }

    /**
     * Returns the index of the given feature.
     * 
     * @param feature
     *            The feature to look up.
     * @return The index of the feature or -1 if the feature is not a child.
     */
    public int getComponentIndex(F feature) {
        return components.indexOf(feature);
    }

    /**
     * Returns the feature at the given position.
     * 
     * @param index
     *            The position of the feature.
     * @return The feature at the given index.
     * @throws IndexOutOfBoundsException
     *             If the index is out of range.
     */
    public F getComponent(int index) throws IndexOutOfBoundsException {
        return components.get(index);
    }


}