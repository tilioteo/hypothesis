/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.io.Serializable;

import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideFascia;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class Document implements Serializable {

	private SlideFascia slideFascia;
	
	public Document(SlideFascia slideFascia) {
		this.slideFascia = slideFascia;
	}
	
	public SlideComponent getComponentById(String id) {
		return slideFascia.getComponent(id);
	}

	public SlideComponent getTimerById(String id) {
		return slideFascia.getTimer(id);
	}

	public SlideComponent getWindowById(String id) {
		return slideFascia.getWindow(id);
	}
	
	public Message createMessage(String uid) {
		return (Message)slideFascia.createMessage(uid);
	}
}
