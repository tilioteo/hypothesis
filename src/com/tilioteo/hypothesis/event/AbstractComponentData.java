/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.interfaces.XmlDataWriter;
import com.vaadin.ui.AbstractComponent;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public abstract class AbstractComponentData<T extends AbstractComponent> implements XmlDataWriter {

	private SlideFascia slideFascia;
	private T sender = null;

	protected AbstractComponentData(T sender, SlideFascia slideFascia) {
		this.sender = sender;
		this.slideFascia = slideFascia;
	}

	public final String getComponentId() {
		return sender != null ? (String) sender.getData() : null;
	}

	public T getSender() {
		return sender;
	}

	public final SlideFascia getSlideManager() {
		return slideFascia;
	}
}
