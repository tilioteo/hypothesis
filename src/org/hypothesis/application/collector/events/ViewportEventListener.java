/**
 * 
 */
package org.hypothesis.application.collector.events;

import java.util.EventListener;




/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface ViewportEventListener extends EventListener {

	public void handleEvent(ViewportEvent event);

}
