/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;
import java.util.EventListener;
import java.util.EventObject;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface ViewportEventListener extends EventListener, Serializable {

	public void handleEvent(EventObject event);

}
