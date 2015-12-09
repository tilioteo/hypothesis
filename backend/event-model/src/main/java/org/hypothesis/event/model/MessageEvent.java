/**
 * 
 */
package org.hypothesis.event.model;

import java.util.Date;
import java.util.EventObject;

import org.hypothesis.event.data.Message;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class MessageEvent extends EventObject {

	private Date timestamp;

	public MessageEvent(Message message) {
		super(message);
		timestamp = new Date();
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public final Message getMessage() {
		return (Message) getSource();
	}
}
