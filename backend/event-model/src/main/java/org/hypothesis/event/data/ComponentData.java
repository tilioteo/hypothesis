/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.data;

import java.io.Serializable;
import java.util.Date;

import com.vaadin.ui.Component;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ComponentData implements Serializable {

	private Component sender;
	private String id;
	private String typeName;
	private String eventName;

	private Date timestamp;
	private Date clientTimestamp = null;

	public Component getSender() {
		return sender;
	}

	public String getId() {
		return id;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getEventName() {
		return eventName;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public Date getClientTimestamp() {
		return clientTimestamp;
	}
}
