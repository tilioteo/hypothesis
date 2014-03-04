/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.common.EntityTableConstants;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for slide
 * 
 */
@Entity
@Table(name = EntityTableConstants.SLIDE_TABLE)
@Access(AccessType.PROPERTY)
public final class Slide extends SerializableIdObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6866522778488675162L;

	/**
	 * parent slide content
	 */
	private SlideContent content;
	private String note;

	protected Slide() {
		super();
	}

	public Slide(SlideContent content) {
		this();
		this.content = content;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EntityTableConstants.SLIDE_GENERATOR)
	@SequenceGenerator(name = EntityTableConstants.SLIDE_GENERATOR, sequenceName = EntityTableConstants.SLIDE_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = EntityFieldConstants.ID)
	public final Long getId() {
		return super.getId();
	}

	// TODO: debug only
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	// @ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@OneToOne(optional = false, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	@JoinColumn(name = EntityFieldConstants.SLIDE_CONTENT_ID, nullable = false, unique = true)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final SlideContent getContent() {
		return content;
	}

	protected void setContent(SlideContent content) {
		this.content = content;
	}

	@Column(name = EntityFieldConstants.NOTE)
	public final String getNote() {
		return note;
	}

	public final void setNote(String note) {
		this.note = note;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Slide))
			return false;
		Slide other = (Slide) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		// TODO remove when Buffered.SourceException occurs
		if (getContent() == null) {
			if (other.getContent() != null)
				return false;
		} else if (!getContent().equals(other.getContent()))
			return false;
		if (getNote() == null) {
			if (other.getNote() != null)
				return false;
		} else if (!getNote().equals(other.getNote()))
			return false;
		/*
		 * if (getTemplate() == null) { if (other.getTemplate() != null) return
		 * false; } else if (!getTemplate().equals(other.getTemplate())) return
		 * false;
		 */
		return true;
	}

	@Override
	public final int hashCode() {
		final int prime = 29;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		// TODO remove when Buffered.SourceException occurs
		result = prime * result
				+ ((getContent() == null) ? 0 : getContent().hashCode());
		result = prime * result
				+ ((getNote() == null) ? 0 : getNote().hashCode());
		// result = prime * result + ((getTemplate() == null) ? 0 :
		// getTemplate().hashCode());
		return result;
	}

}
