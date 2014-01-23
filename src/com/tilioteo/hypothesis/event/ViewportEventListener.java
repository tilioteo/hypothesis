/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.util.EventListener;




/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface ViewportEventListener extends EventListener {

	public void handleEvent(ViewportEvent event);

}
