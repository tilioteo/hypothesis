/**
 * 
 */
package com.tilioteo.hypothesis.data.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * @author kamil
 *
 */
@Entity
@Table(name = TableConstants.MESSAGE_TABLE)
@Access(AccessType.PROPERTY)
public class Message extends SerializableUidObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3012187687893640494L;

	/**
	 * raw xml string of message definition
	 */
	private String data;

	private String note;

	@Override
	@Id
	@Column(name = FieldConstants.UID)
	public String getUid() {
		return super.getUid();
	}

	@Column(name = FieldConstants.XML_DATA, nullable = false)
	@Type(type = "text")
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Column(name = FieldConstants.NOTE)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Message)) {
			return false;
		}
		Message other = (Message) obj;

		String uid = getUid();
		String uid2 = other.getUid();
		String xmlData = getData();
		String xmlData2 = other.getData();
		String note = getNote();
		String note2 = other.getNote();

		if (uid == null) {
			if (uid2 != null) {
				return false;
			}
		} else if (!uid.equals(uid2)) {
			return false;
		}

		if (xmlData == null) {
			if (xmlData2 != null) {
				return false;
			}
		} else if (!xmlData.equals(xmlData2)) {
			return false;
		}

		if (note == null) {
			if (note2 != null) {
				return false;
			}
		} else if (!note.equals(note2)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		String uid = getUid();
		String xmlData = getData();
		String note = getNote();

		final int prime = 71;
		int result = 1;
		result = prime * result + (uid != null ? uid.hashCode() : 0);
		result = prime * result + (xmlData != null ? xmlData.hashCode() : 0);
		result = prime * result + (note != null ? note.hashCode() : 0);
		return result;
	}

}
