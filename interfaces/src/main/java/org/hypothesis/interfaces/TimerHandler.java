/**
 * 
 */
package org.hypothesis.interfaces;

import com.vaadin.ui.AbstractComponent;

/**
 * @author morongk
 *
 */
public interface TimerHandler {

	void addTimer(AbstractComponent timer);
	
	boolean removeTimer(AbstractComponent timer);
	
	void removeAllTimers();
}
