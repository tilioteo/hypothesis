package org.hypothesis.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.HasId;
import org.hypothesis.data.interfaces.TableConstants;

@SuppressWarnings("serial")
@Entity
@Table(name = TableConstants.SLIDE_TEMPLATE_TABLE)
@Access(AccessType.PROPERTY)
public class SlideTemplate implements Serializable, HasId<String> {

	private String id;

	/**
	 * serialized data of slide template
	 */
	private String data;

	private String note;

	@Override
	@Id
	@Column(name = FieldConstants.UID)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SlideTemplate == false)
			return false;

		final SlideTemplate other = (SlideTemplate) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(getData(), other.getData()))
			return false;
		if (!Objects.equals(getNote(), other.getNote()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SlideTemplate [id=" + id + ", note=" + note + ", data=" + data + "]";
	}

}
