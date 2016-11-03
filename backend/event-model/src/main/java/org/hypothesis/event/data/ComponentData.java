/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.data;

import com.vaadin.ui.Component;

import java.io.Serializable;

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
}
