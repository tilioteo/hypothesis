/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.util.EventListener;
import java.util.EventObject;

/**
 * @author kamil
 *
 */
public interface MessageEventListener extends EventListener {

	public void handleEvent(EventObject event);

}
