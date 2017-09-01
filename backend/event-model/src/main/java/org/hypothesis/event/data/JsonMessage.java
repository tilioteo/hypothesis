/**
 * 
 */
package org.hypothesis.event.data;

import java.io.Serializable;

import elemental.json.Json;
import elemental.json.JsonObject;

/**
 * @author morongk
 *
 */
@SuppressWarnings("serial")
public abstract class JsonMessage implements Serializable {
	
	protected static final String MESSAGE_CLASS = "CLASS";

	protected JsonObject json;
	
	protected void initJson() {
		json = Json.createObject();
		json.put(MESSAGE_CLASS, this.getClass().getName());
	}

	@Override
	public String toString() {
		return json != null ? json.toJson() : "<not initialized>";
	}

}
