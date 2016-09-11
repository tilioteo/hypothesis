/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.data;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import elemental.json.Json;
import elemental.json.JsonObject;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class Message implements Serializable {

	private static final String MESSAGE_CLASS = "CLASS";
	private static final String MESSAGE_UID = "UID";
	private static final String MESSAGE_SENDER = "SENDER";
	private static final String MESSAGE_RECEIVER = "RECEIVER";
	private static final String MESSAGE_DEFINITIONS = "DEFS";
	private static final String MESSAGE_DATA = "DATA";
	private static final String MESSAGE_TIMESTAMP = "TIMESTAMP";

	private JsonObject json;
	private JsonObject defs;
	private JsonObject data;

	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.ENGLISH);

	protected Message() {
	}

	public Message(String uid, Long senderId) {
		json = Json.createObject();

		json.put(MESSAGE_CLASS, this.getClass().getName());
		json.put(MESSAGE_UID, uid);
		if (senderId != null) {
			json.put(MESSAGE_SENDER, senderId);
		} else {
			json.put(MESSAGE_SENDER, Json.createNull());
		}

		defs = Json.createObject();
		json.put(MESSAGE_DEFINITIONS, defs);

		data = Json.createObject();
		json.put(MESSAGE_DATA, data);
	}

	protected void setPropertyDefinition(String name, Class<?> clazz) {
		defs.put(name, clazz.getName());
	}

	public void setProperty(String name, Object value) {
		if (defs.hasKey(name)) {
			if (value != null) {
				String className = defs.getString(name);
				Class<?> clazz = null;
				try {
					clazz = Class.forName(className);
				} catch (ClassNotFoundException e) {
					return;
				}
				if (!clazz.isAssignableFrom(value.getClass())) {
					try {
						value = clazz.cast(value);
					} catch (ClassCastException e) {
						return;
					}
				}

				setData(name, clazz, value);
			} else {
				data.put(name, Json.createNull());
			}
		}
	}

	public void setProperty(String name, int value) {
		setProperty(name, (Integer) value);
	}

	public void setProperty(String name, double value) {
		setProperty(name, (Double) value);
	}

	public void setProperty(String name, boolean value) {
		setProperty(name, (Boolean) value);
	}

	private void setData(String name, Class<?> clazz, Object value) {
		if (Date.class.isAssignableFrom(clazz)) {
			Date date = (Date) value;
			data.put(name, dateToString(date));
		} else if (Number.class.isAssignableFrom(clazz)) {
			data.put(name, ((Number) value).doubleValue());
		} else if (Boolean.class.isAssignableFrom(clazz)) {
			data.put(name, (boolean) value);
		} else if (String.class.isAssignableFrom(clazz)) {
			data.put(name, (String) value);
		}
	}

	public Object getProperty(String name) {
		if (defs.hasKey(name)) {
			String className = defs.getString(name);
			Class<?> clazz = null;
			try {
				clazz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				return null;
			}

			try {
				if (Date.class.isAssignableFrom(clazz)) {
					return stringToDate(data.getString(name));
				} else if (Integer.class.isAssignableFrom(clazz)) {
					return new Integer((int) Math.round(data.getNumber(name)));
					// return clazz.cast(data.getNumber(name));
				} else if (Double.class.isAssignableFrom(clazz)) {
					return clazz.cast(data.getNumber(name));
				} else if (Boolean.class.isAssignableFrom(clazz)) {
					return data.getBoolean(name);
				} else if (String.class.isAssignableFrom(clazz)) {
					return data.getString(name);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getUid() {
		return json.getString(MESSAGE_UID);
	}

	public Long getSenderId() {
		try {
			return (long) json.getNumber(MESSAGE_SENDER);
		} catch (Exception e) {}

		return null;
	}

	public Long getReceiverId() {
		try {
			return (long) json.getNumber(MESSAGE_RECEIVER);
		} catch (Exception e) {}

		return null;
	}

	public void setReceiverId(Long receiverId) {
		if (receiverId != null) {
			json.put(MESSAGE_RECEIVER, receiverId);
		} else {
			json.put(MESSAGE_RECEIVER, Json.createNull());
		}
	}

	private Date stringToDate(String string) {
		try {
			return dateFormat.parse(string);
		} catch (ParseException e) {}
		
		return null;
	}

	private String dateToString(Date date) {
		if (date != null) {
			return dateFormat.format(date);
		}
		return null;
	}

	public Date getTimestamp() {
		try {
			String str = json.getString(MESSAGE_TIMESTAMP);

			return stringToDate(str);
		} catch (Exception e) {}

		return null;
	}

	public void updateTimestamp() {
		json.put(MESSAGE_TIMESTAMP, dateToString(new Date()));
	}

	@Override
	public String toString() {
		return json.toJson();
	}

	public static final Message fromJson(String string) {
		JsonObject json = null;
		try {
			json = Json.parse(string);
			String className = json.getString(MESSAGE_CLASS);
			if (Message.class.getName().equals(className)) {
				JsonObject defs = json.getObject(MESSAGE_DEFINITIONS);
				JsonObject data = json.getObject(MESSAGE_DATA);

				if (defs != null && data != null) {
					Message message = new Message();
					message.json = json;
					message.defs = defs;
					message.data = data;

					return message;
				}
			}

		} catch (Exception e) {}

		return null;
	}
}
