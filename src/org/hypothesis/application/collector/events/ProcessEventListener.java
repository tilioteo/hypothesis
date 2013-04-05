/**
 * 
 */
package org.hypothesis.application.collector.events;

import java.util.EventListener;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface ProcessEventListener extends EventListener {

	void handleEvent(AbstractProcessEvent event);

}
