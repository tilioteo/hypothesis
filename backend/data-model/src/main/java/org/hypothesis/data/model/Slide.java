package org.hypothesis.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.HasId;
import org.hypothesis.data.interfaces.TableConstants;

@SuppressWarnings("serial")
@Entity
@Table(name = TableConstants.SLIDE_TABLE)
@Access(AccessType.PROPERTY)
public class Slide implements Serializable, HasId<Long> {

	private Long id;

	/**
	 * the parent slide template
	 */
	private SlideTemplate template;

	/**
	 * serialized data of slide content
	 */
	private String data;

	private String note;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.SLIDE_GENERATOR)
	@SequenceGenerator(name = TableConstants.SLIDE_GENERATOR, sequenceName = TableConstants.SLIDE_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = FieldConstants.SLIDE_TEMPLATE_UID, nullable = false)
	public SlideTemplate getTemplate() {
		return template;
	}

	public void setTemplate(SlideTemplate slideTemplate) {
		this.template = slideTemplate;
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
		if (obj instanceof Slide == false)
			return false;

		final Slide other = (Slide) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(getTemplate(), other.getTemplate()))
			return false;
		if (!Objects.equals(getData(), other.getData()))
			return false;
		if (!Objects.equals(getNote(), other.getNote()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Slide [id=" + id + ", note=" + note + ", data=" + data + ", template=" + template + "]";
	}

}
