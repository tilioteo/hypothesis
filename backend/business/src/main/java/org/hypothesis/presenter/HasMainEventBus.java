/**
 * 
 */
package org.hypothesis.presenter;

import org.hypothesis.eventbus.MainEventBus;

/**
 * @author kamil
 *
 */
public interface HasMainEventBus {

	public void setMainEventBus(MainEventBus bus);

}
