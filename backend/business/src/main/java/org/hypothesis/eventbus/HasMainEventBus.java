/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.eventbus;

import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface HasMainEventBus extends Serializable {

	/**
	 * set main event bus object
	 * 
	 * @param bus
	 */
	public void setMainEventBus(MainEventBus bus);

	/**
	 * get main event bus object
	 * 
	 * @return
	 */
	public MainEventBus getMainEventBus();

}
