/**
 * 
 */
package org.vaadin.maps.ui;


import com.vaadin.ui.Component;


/**
 * @author morong
 *
 */
public interface ComponentContainer<C extends Component> extends HasComponents<C>, com.vaadin.ui.HasComponents.ComponentAttachDetachNotifier {

    /**
     * Adds the component into this container.
     * 
     * @param c
     *            the component to be added.
     */
    public void addComponent(C c);

    /**
     * Adds the components in the given order to this component container.
     * 
     * @param components
     *            The components to add.
     */
    @SuppressWarnings("unchecked")
	public void addComponents(C... components);

    /**
     * Removes the component from this container.
     * 
     * @param c
     *            the component to be removed.
     */
    public void removeComponent(C c);

    /**
     * Removes all components from this container.
     */
    public void removeAllComponents();

    /**
     * Replaces the component in the container with another one without changing
     * position.
     * 
     * <p>
     * This method replaces component with another one is such way that the new
     * component overtakes the position of the old component. If the old
     * component is not in the container, the new component is added to the
     * container. If the both component are already in the container, their
     * positions are swapped. Component attach and detach events should be taken
     * care as with add and remove.
     * </p>
     * 
     * @param oldComponent
     *            the old component that will be replaced.
     * @param newComponent
     *            the new component to be replaced.
     */
    public void replaceComponent(C oldComponent, C newComponent);

    /**
     * Gets the number of children this {@link ComponentContainer} has. This
     * must be symmetric with what {@link #getComponentIterator()} returns.
     * 
     * @return The number of child components this container has.
     * @since 7.0.0
     */
    public int getComponentCount();

    /**
     * Moves all components from an another container into this container. The
     * components are removed from <code>source</code>.
     * 
     * @param source
     *            the container which contains the components that are to be
     *            moved to this container.
     */
    public void moveComponentsFrom(ComponentContainer<C> source);

}
