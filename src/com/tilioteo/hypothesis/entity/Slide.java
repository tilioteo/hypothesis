/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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
	/*@Override
	public void setId(Long id) {
		this.id = id;
	}*/

	@OneToOne(optional = false)
	@JoinColumn(name = EntityFieldConstants.SLIDE_CONTENT_ID, nullable = false, unique = true)
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Slide)) {
			return false;
		}
		Slide other = (Slide) obj;

		Long id = getId();
		Long id2 = other.getId();
		SlideContent content = getContent();
		SlideContent content2 = other.getContent();
		String note = getNote();
		String note2 = other.getNote();

		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (content != null && !content.equals(content2)) {
			return false;
		} else if (content2 != null) {
			return false;
		}
		
		if (note != null && !note.equals(note2)) {
			return false;
		} else if (note2 != null) {
			return false;
		}
		
		return true;
	}

	@Override
	public final int hashCode() {
		Long id = getId();
		SlideContent content = getContent();
		String note = getNote();

		final int prime = 29;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result	+ (content != null ? getContent().hashCode() : 0);
		result = prime * result	+ (note != null ? getNote().hashCode() : 0);

		return result;
	}

}
