/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.io.Serializable;

import elemental.json.Json;
import elemental.json.JsonObject;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ProcessUIMessage implements Serializable {
	
	private static final String MESSAGE_CLASS = "CLASS";
	private static final String MESSAGE_RECEIVER = "RECEIVER";

	private JsonObject json;
	
	protected ProcessUIMessage() {
	}
	
	public ProcessUIMessage(String receiver) {
		json = Json.createObject();
		
		json.put(MESSAGE_CLASS, this.getClass().getName());
		json.put(MESSAGE_RECEIVER, receiver);
	}

	public String getReceiver() {
		try {
			String receiver = json.getString(MESSAGE_RECEIVER);
			return receiver;
		} catch (Throwable e) {}

		return null; 
	}
	
	@Override
	public String toString() {
		return json.toJson();
	}
	
	public static ProcessUIMessage fromJson(String string) {
		JsonObject json = null;
		try {
			json = Json.parse(string);
			String className = json.getString(MESSAGE_CLASS);
			if (Message.class.getName().equals(className)) {
				ProcessUIMessage message = new ProcessUIMessage();
				message.json = json;
					
				return message;
			}
			
		} catch (Throwable e) {}
		
		return null;
	}
}
