/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.io.Serializable;
import java.util.EventListener;




/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface ViewportEventListener extends EventListener, Serializable {

	public void handleEvent(ViewportEvent event);

}
