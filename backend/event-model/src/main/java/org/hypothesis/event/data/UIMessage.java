/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class UIMessage extends JsonMessage {

	private static final String MESSAGE_TYPE = "TYPE";
	private static final String MESSAGE_GROUP = "GROUP";
	private static final String MESSAGE_USER = "USER";
	private static final String MESSAGE_SENDER = "SENDER";
	private static final String MESSAGE_ROLES = "ROLES";

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
		} catch (Throwable e) {
		}

		return null;
	}

	public Long getGroupId() {
		try {
			return (Long) (long) json.getNumber(MESSAGE_GROUP);
		} catch (Throwable e) {
		}

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
		} catch (Throwable e) {
		}

		return null;
	}

	public void setUserId(Long userId) {
		if (userId != null) {
			json.put(MESSAGE_USER, userId);
		} else {
			json.put(MESSAGE_USER, Json.createNull());
		}
	}

	public Long getSenderId() {
		try {
			return (Long) (long) json.getNumber(MESSAGE_SENDER);
		} catch (Throwable e) {
		}

		return null;
	}

	public void setSenderId(Long userId) {
		if (userId != null) {
			json.put(MESSAGE_SENDER, userId);
		} else {
			json.put(MESSAGE_SENDER, Json.createNull());
		}
	}

	public void setRoles(String... roles) {
		if (roles != null && roles.length > 0) {
			json.put(MESSAGE_ROLES, toJsonArray(roles));
		} else {
			json.put(MESSAGE_ROLES, Json.createNull());
		}
	}

	public List<String> getRoles() {
		try {
			return toStringList(json.getArray(MESSAGE_ROLES));
		} catch (Throwable e) {
		}

		return Collections.emptyList();
	}

	private List<String> toStringList(JsonArray array) {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < array.length(); ++i) {
			list.add(array.getString(i));
		}

		return list;
	}

	private JsonArray toJsonArray(String[] values) {
		JsonArray array = Json.createArray();
		for (int i = 0; i < values.length; ++i) {
			array.set(i, values[i]);
		}
		return array;
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

		} catch (Throwable e) {
		}

		return null;
	}
}
