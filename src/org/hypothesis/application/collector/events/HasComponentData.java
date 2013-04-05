/**
 * 
 */
package org.hypothesis.application.collector.events;

import com.vaadin.ui.AbstractComponent;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface HasComponentData<T extends AbstractComponent> {

	public AbstractComponentData<T> getComponentData();

}
