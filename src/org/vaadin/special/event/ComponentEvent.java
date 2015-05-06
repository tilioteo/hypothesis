/**
 * 
 */
package org.vaadin.special.event;

import java.util.Date;

import com.vaadin.ui.Component;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class ComponentEvent extends Component.Event {

	private long clientTimestamp;
	private Date serverTimestamp;

	protected ComponentEvent(long timestamp, Component source) {
		super(source);
		this.serverTimestamp = new Date();
		this.clientTimestamp = timestamp;
	}

	public long getTimestamp() {
		return clientTimestamp;
	}

	public Date getClientDatetime() {
		return new Date(clientTimestamp);
	}

	public long getServerTimestamp() {
		return serverTimestamp.getTime();
	}

	public Date getServerDatetime() {
		return serverTimestamp;
	}

}
