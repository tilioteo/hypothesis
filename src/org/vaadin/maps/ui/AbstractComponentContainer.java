/**
 * 
 */
package org.vaadin.maps.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.vaadin.server.ComponentSizeValidator;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;


/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractComponentContainer<C extends Component> extends AbstractComponent implements ComponentContainer<C> {

    /**
     * Constructs a new component container.
     */
    public AbstractComponentContainer() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.maps.ui.ComponentContainer<C>#addComponents(C[])
     */
    @SuppressWarnings("unchecked")
	@Override
    public void addComponents(C... components) {
        for (C c : components) {
            addComponent(c);
        }
    }

    /**
     * Removes all components from the container. This should probably be
     * re-implemented in extending classes for a more powerful implementation.
     */
    @Override
    public void removeAllComponents() {
        final LinkedList<C> l = new LinkedList<C>();

        // Adds all components
        for (final Iterator<C> i = typedIterator(); i.hasNext();) {
            l.add(i.next());
        }

        // Removes all component
        for (final Iterator<C> i = l.iterator(); i.hasNext();) {
            removeComponent(i.next());
        }
    }

    /*
     * Moves all components from an another container into this container. Don't
     * add a JavaDoc comment here, we use the default documentation from
     * implemented interface.
     */
    @Override
    public void moveComponentsFrom(ComponentContainer<C> source) {
        final LinkedList<C> components = new LinkedList<C>();
        for (final Iterator<C> i = source.typedIterator(); i.hasNext();) {
            components.add(i.next());
        }

        for (final Iterator<C> i = components.iterator(); i.hasNext();) {
            final C c = i.next();
            source.removeComponent(c);
            addComponent(c);
        }
    }

    /* documented in interface */
    @Override
    public void addComponentAttachListener(ComponentAttachListener listener) {
        addListener(ComponentAttachEvent.class, listener,
                ComponentAttachListener.attachMethod);
    }

    /* documented in interface */
    @Override
    public void removeComponentAttachListener(ComponentAttachListener listener) {
        removeListener(ComponentAttachEvent.class, listener,
                ComponentAttachListener.attachMethod);
    }

    /* documented in interface */
    @Override
    public void addComponentDetachListener(ComponentDetachListener listener) {
        addListener(ComponentDetachEvent.class, listener,
                ComponentDetachListener.detachMethod);
    }

    /* documented in interface */
    @Override
    public void removeComponentDetachListener(ComponentDetachListener listener) {
        removeListener(ComponentDetachEvent.class, listener,
                ComponentDetachListener.detachMethod);
    }

    /**
     * Fires the component attached event. This should be called by the
     * addComponent methods after the component have been added to this
     * container.
     * 
     * @param component
     *            the component that has been added to this container.
     */
    protected void fireComponentAttachEvent(C component) {
        fireEvent(new ComponentAttachEvent(this, component));
    }

    /**
     * Fires the component detached event. This should be called by the
     * removeComponent methods after the component have been removed from this
     * container.
     * 
     * @param component
     *            the component that has been removed from this container.
     */
    protected void fireComponentDetachEvent(C component) {
        fireEvent(new ComponentDetachEvent(this, component));
    }

    /**
     * This only implements the events and component parent calls. The extending
     * classes must implement component list maintenance and call this method
     * after component list maintenance.
     * 
     * @see org.vaadin.maps.ui.ComponentContainer#addComponent(Component)
     */
    @Override
    public void addComponent(C c) {
        if (c instanceof com.vaadin.ui.ComponentContainer || c instanceof ComponentContainer<?>) {
            // Make sure we're not adding the component inside it's own content
            for (Component parent = this; parent != null; parent = parent.getParent()) {
                if (parent == c) {
                    throw new IllegalArgumentException("Component cannot be added inside it's own content.");
                }
            }
        }

        if (c.getParent() != null) {
            // If the component already has a parent, try to remove it
            com.vaadin.ui.AbstractSingleComponentContainer.removeFromParent(c);
        }

        c.setParent((HasComponents<C>) this);
        fireComponentAttachEvent(c);
    }

    /**
     * This only implements the events and component parent calls. The extending
     * classes must implement component list maintenance and call this method
     * before component list maintenance.
     * 
     * @see org.vaadin.maps.ui.ComponentContainer#removeComponent(Component)
     */
    @Override
    public void removeComponent(C c) {
        if (c.getParent() == this) {
            c.setParent(null);
            fireComponentDetachEvent(c);
        }
    }

    @Override
    public void setWidth(float width, Unit unit) {
        /*
         * child tree repaints may be needed, due to our fall back support for
         * invalid relative sizes
         */
        Collection<C> dirtyChildren = null;
        boolean childrenMayBecomeUndefined = false;
        if (getWidth() == SIZE_UNDEFINED && width != SIZE_UNDEFINED) {
            // children currently in invalid state may need repaint
            dirtyChildren = getInvalidSizedChildren(false);
        } else if ((width == SIZE_UNDEFINED && getWidth() != SIZE_UNDEFINED)
                || (unit == Unit.PERCENTAGE
                        && getWidthUnits() != Unit.PERCENTAGE && !ComponentSizeValidator
                            .parentCanDefineWidth(this))) {
            /*
             * relative width children may get to invalid state if width becomes
             * invalid. Width may also become invalid if units become percentage
             * due to the fallback support
             */
            childrenMayBecomeUndefined = true;
            dirtyChildren = getInvalidSizedChildren(false);
        }
        super.setWidth(width, unit);
        repaintChangedChildTrees(dirtyChildren, childrenMayBecomeUndefined,
                false);
    }

    private void repaintChangedChildTrees(
            Collection<C> invalidChildren,
            boolean childrenMayBecomeUndefined, boolean vertical) {
        if (childrenMayBecomeUndefined) {
            Collection<C> previouslyInvalidComponents = invalidChildren;
            invalidChildren = getInvalidSizedChildren(vertical);
            if (previouslyInvalidComponents != null && invalidChildren != null) {
                for (Iterator<C> iterator = invalidChildren.iterator(); iterator.hasNext();) {
                    Component component = iterator.next();
                    if (previouslyInvalidComponents.contains(component)) {
                        // still invalid don't repaint
                        iterator.remove();
                    }
                }
            }
        } else if (invalidChildren != null) {
            Collection<C> stillInvalidChildren = getInvalidSizedChildren(vertical);
            if (stillInvalidChildren != null) {
                for (C component : stillInvalidChildren) {
                    // didn't become valid
                    invalidChildren.remove(component);
                }
            }
        }
        if (invalidChildren != null) {
            repaintChildTrees(invalidChildren);
        }
    }

    @SuppressWarnings("unchecked")
	private Collection<C> getInvalidSizedChildren(final boolean vertical) {
        HashSet<C> components = null;
        for (Component component : this) {
            boolean valid = vertical ? ComponentSizeValidator
                    .checkHeights(component) : ComponentSizeValidator
                    .checkWidths(component);
            if (!valid) {
                if (components == null) {
                    components = new HashSet<C>();
                }
                components.add((C) component);
            }
        }
        return components;
    }

    private void repaintChildTrees(Collection<C> dirtyChildren) {
        for (C c : dirtyChildren) {
            c.markAsDirtyRecursive();
        }
    }

    @Override
    public void setHeight(float height, Unit unit) {
        /*
         * child tree repaints may be needed, due to our fall back support for
         * invalid relative sizes
         */
        Collection<C> dirtyChildren = null;
        boolean childrenMayBecomeUndefined = false;
        if (getHeight() == SIZE_UNDEFINED && height != SIZE_UNDEFINED) {
            // children currently in invalid state may need repaint
            dirtyChildren = getInvalidSizedChildren(true);
        } else if ((height == SIZE_UNDEFINED && getHeight() != SIZE_UNDEFINED)
                || (unit == Unit.PERCENTAGE
                        && getHeightUnits() != Unit.PERCENTAGE && !ComponentSizeValidator
                            .parentCanDefineHeight(this))) {
            /*
             * relative height children may get to invalid state if height
             * becomes invalid. Height may also become invalid if units become
             * percentage due to the fallback support.
             */
            childrenMayBecomeUndefined = true;
            dirtyChildren = getInvalidSizedChildren(true);
        }
        super.setHeight(height, unit);
        repaintChangedChildTrees(dirtyChildren, childrenMayBecomeUndefined,
                true);
    }

}
