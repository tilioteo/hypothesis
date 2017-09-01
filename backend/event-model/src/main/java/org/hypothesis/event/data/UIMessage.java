/**
 * 
 */
package org.hypothesis.event.data;

import elemental.json.Json;
import elemental.json.JsonObject;

/**
 * @author morongk
 *
 */
@SuppressWarnings("serial")
public class UIMessage extends JsonMessage {
	
	private static final String MESSAGE_TYPE = "TYPE";
	private static final String MESSAGE_GROUP = "GROUP";
	private static final String MESSAGE_USER = "USER";
	
	protected UIMessage() {
	}

	public UIMessage(String type) {
		initJson();
		
		setType(type);
	}
	
	protected void setType(String type) {
		if (type != null) {
			json.put(MESSAGE_TYPE, type);
		} else {
			json.put(MESSAGE_TYPE, Json.createNull());
		}
	}
	
	public String getType() {
		try {
			return json.getString(MESSAGE_TYPE);
		} catch (Throwable e) {}

		return null;
	}
	
	public Long getGroupId() {
		try {
			return (Long) (long) json.getNumber(MESSAGE_GROUP);
		} catch (Throwable e) {}

		return null;
	}

	public void setGroupId(Long groupId) {
		if (groupId != null) {
			json.put(MESSAGE_GROUP, groupId);
		} else {
			json.put(MESSAGE_GROUP, Json.createNull());
		}
	}
	
	public Long getUserId() {
		try {
			return (Long) (long) json.getNumber(MESSAGE_USER);
		} catch (Throwable e) {}

		return null;
	}

	public void setUserId(Long userId) {
		if (userId != null) {
			json.put(MESSAGE_USER, userId);
		} else {
			json.put(MESSAGE_USER, Json.createNull());
		}
	}
	
	public static final UIMessage fromJson(String string) {
		JsonObject json = null;
		try {
			json = Json.parse(string);
			String className = json.getString(MESSAGE_CLASS);
			if (UIMessage.class.getName().equals(className)) {
				UIMessage message = new UIMessage();
				message.json = json;

				return message;
			}

		} catch (Throwable e) {}

		return null;
	}
}
