/**
 * 
 */
package org.vaadin.maps.ui.handler;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public interface CanHandleLine {

	void insertXY(Number x, Number y);
	void insertDeltaXY(Number x, Number y);
	void insertDirectionLength(Number direction, Number length);
	void insertDeflectionLength(Number deflection, Number length);
	
}
