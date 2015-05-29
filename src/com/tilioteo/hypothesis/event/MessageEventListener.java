/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.io.Serializable;
import java.util.EventListener;

/**
 * @author kamil
 *
 */
public interface MessageEventListener extends EventListener, Serializable {

	public void handleEvent(MessageEvent event);

}
