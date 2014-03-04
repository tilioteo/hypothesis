/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.common.EntityTableConstants;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for task
 * 
 */
@Entity
@Table(name = EntityTableConstants.TASK_TABLE)
@Access(AccessType.PROPERTY)
public final class Task extends SerializableIdObject implements HasList<Slide> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8380379924205819447L;

	private String name;
	private String note;

	/**
	 * list of slides
	 */
	private List<Slide> slides = new LinkedList<Slide>();

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EntityTableConstants.TASK_GENERATOR)
	@SequenceGenerator(name = EntityTableConstants.TASK_GENERATOR, sequenceName = EntityTableConstants.TASK_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = EntityFieldConstants.ID)
	public final Long getId() {
		return super.getId();
	}

	@Column(name = EntityFieldConstants.NAME, nullable = false, unique = false)
	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	@Column(name = EntityFieldConstants.NOTE)
	public final String getNote() {
		return note;
	}

	public final void setNote(String note) {
		this.note = note;
	}

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = EntityTableConstants.TASK_SLIDE_TABLE, joinColumns = @JoinColumn(name = EntityFieldConstants.TASK_ID), inverseJoinColumns = @JoinColumn(name = EntityFieldConstants.SLIDE_ID))
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@OrderColumn(name = EntityFieldConstants.RANK)
	public final List<Slide> getSlides() {
		return slides;
	}

	protected void setSlides(List<Slide> list) {
		this.slides = list;
	}

	@Transient
	public final List<Slide> getList() {
		return slides;
	}

	public final void addSlide(Slide slide) {
		if (slide != null)
			this.slides.add(slide);
	}

	public final void removeSlide(Slide slide) {
		this.slides.remove(slide);
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Task))
			return false;
		Task other = (Task) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getName()))
			return false;
		// TODO remove when Buffered.SourceException occurs
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		if (getNote() == null) {
			if (other.getNote() != null)
				return false;
		} else if (!getNote().equals(other.getNote()))
			return false;
		if (getSlides() == null) {
			if (other.getSlides() != null)
				return false;
		} else if (!getSlides().equals(other.getSlides()))
			return false;
		return true;
	}

	@Override
	public final int hashCode() {
		final int prime = 43;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		// TODO remove when Buffered.SourceException occurs
		result = prime * result
				+ ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result
				+ ((getNote() == null) ? 0 : getNote().hashCode());
		result = prime * result
				+ ((getSlides() == null) ? 0 : getSlides().hashCode());
		return result;
	}

}
