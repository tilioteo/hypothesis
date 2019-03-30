/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import com.vaadin.ui.AbstractComponent;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface TimerHandler {

	void addTimer(AbstractComponent timer);
	
	boolean removeTimer(AbstractComponent timer);
	
	void removeAllTimers();
}
