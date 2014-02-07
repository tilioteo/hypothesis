/**
 * 
 */
package org.vaadin.maps.ui;

import java.util.Iterator;

import com.vaadin.ui.Component;

/**
 * @author morong
 *
 */
public interface HasComponents<C extends Component> extends com.vaadin.ui.HasComponents {
    /**
     * Gets an iterator to the collection of contained components. Using this
     * iterator it is possible to step through all components contained in this
     * container.
     * 
     * @return the component iterator.
     */
    public Iterator<C> typedIterator();

}
