/**
 * 
 */
package com.tilioteo.hypothesis.event.interfaces;

import java.io.Serializable;
import java.util.EventListener;

import com.tilioteo.hypothesis.event.model.ViewportEvent;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface ViewportEventListener extends EventListener, Serializable {

	public void handleEvent(ViewportEvent event);

}
