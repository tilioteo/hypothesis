/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import org.hypothesis.event.data.Message;

import java.util.Date;
import java.util.EventObject;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class MessageEvent extends EventObject {

	private final Date timestamp;

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
