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

	MainEventBus getBus();

	void setBus(MainEventBus bus);

}
