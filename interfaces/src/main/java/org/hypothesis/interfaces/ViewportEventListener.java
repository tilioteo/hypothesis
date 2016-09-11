/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import java.io.Serializable;
import java.util.EventListener;
import java.util.EventObject;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface ViewportEventListener extends EventListener, Serializable {

	void handleEvent(EventObject event);

}
