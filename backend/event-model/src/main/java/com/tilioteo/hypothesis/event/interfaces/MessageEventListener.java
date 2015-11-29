/**
 * 
 */
package com.tilioteo.hypothesis.event.interfaces;

import java.util.EventListener;

import com.tilioteo.hypothesis.event.model.MessageEvent;

/**
 * @author kamil
 *
 */
public interface MessageEventListener extends EventListener {

	public void handleEvent(MessageEvent event);

}
