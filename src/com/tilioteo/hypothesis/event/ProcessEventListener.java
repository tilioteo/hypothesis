/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.util.EventListener;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface ProcessEventListener extends EventListener {

	void handleEvent(ProcessEvent event);

}
