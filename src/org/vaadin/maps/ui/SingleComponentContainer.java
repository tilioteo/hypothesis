/**
 * 
 */
package org.vaadin.maps.ui;

import com.vaadin.ui.Component;

/**
 * @author morong
 *
 */
public interface SingleComponentContainer<C extends Component> extends HasComponents<C>, com.vaadin.ui.HasComponents.ComponentAttachDetachNotifier {

    /**
     * Gets the number of children this {@link SingleComponentContainer} has.
     * This must be symmetric with what {@link #iterator()} returns and thus
     * typically return 1 if the content is set, 0 otherwise.
     * 
     * @return The number of child components this container has.
     */
    public int getComponentCount();

    /**
     * Gets the content of this container. The content is a component that
     * serves as the outermost item of the visual contents.
     * 
     * @return a component to use as content
     * 
     * @see #setContent(C)
     */
    public C getContent();

    /**
     * Sets the content of this container. The content is a component that
     * serves as the outermost item of the visual contents.
     * 
     * The content should always be set, either as a constructor parameter or by
     * calling this method.
     * 
     * @return a component (typically a layout) to use as content
     */
    public void setContent(C content);

}
