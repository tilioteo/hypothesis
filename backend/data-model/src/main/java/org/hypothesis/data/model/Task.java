/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.model;

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
import org.hibernate.annotations.Type;
import org.hypothesis.data.interfaces.HasList;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@Entity
@Table(name = TableConstants.TASK_TABLE)
@Access(AccessType.PROPERTY)
public final class Task extends SerializableIdObject implements HasList<Slide> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8380379924205819447L;

	private String name;
	private String note;

	/**
	 * serialized data for task
	 */
	private String data;

	private boolean randomized;

	/**
	 * list of slides
	 */
	private List<Slide> slides = new LinkedList<>();

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.TASK_GENERATOR)
	@SequenceGenerator(name = TableConstants.TASK_GENERATOR, sequenceName = TableConstants.TASK_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return super.getId();
	}

	@Column(name = FieldConstants.NAME, nullable = false, unique = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = FieldConstants.NOTE)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Column(name = FieldConstants.XML_DATA)
	@Type(type = "text")
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Column(name = FieldConstants.RANDOMIZED)
	public boolean isRandomized() {
		return randomized;
	}

	public void setRandomized(Boolean randomized) {
		this.randomized = randomized != null ? randomized : false;
	}

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = TableConstants.TASK_SLIDE_TABLE, joinColumns = @JoinColumn(name = FieldConstants.TASK_ID) , inverseJoinColumns = @JoinColumn(name = FieldConstants.SLIDE_ID) )
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.TRUE)
	@OrderColumn(name = FieldConstants.RANK)
	public List<Slide> getSlides() {
		return slides;
	}

	protected void setSlides(List<Slide> list) {
		this.slides = list;
	}

	@Transient
	public final List<Slide> getList() {
		return getSlides();
	}

	public final void addSlide(Slide slide) {
		if (slide != null) {
			getSlides().add(slide);
		}
	}

	public final void removeSlide(Slide slide) {
		getSlides().remove(slide);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Task)) {
			return false;
		}
		Task other = (Task) obj;

		Long id = getId();
		Long id2 = other.getId();
		String name = getName();
		String name2 = other.getName();
		String note = getNote();
		String note2 = other.getNote();
		boolean randomized = isRandomized();
		boolean randomized2 = other.isRandomized();
		// List<Slide> slides = getSlides();
		// List<Slide> slides2 = other.getSlides();

		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (name == null) {
			if (name2 != null) {
				return false;
			}
		} else if (!name.equals(name2)) {
			return false;
		}

		if (note == null) {
			if (note2 != null) {
				return false;
			}
		} else if (!note.equals(note2)) {
			return false;
		}

		return randomized == randomized2;

	}

	@Override
	public int hashCode() {
		Long id = getId();
		String name = getName();
		String note = getNote();
		boolean randomized = isRandomized();
		List<Slide> slides = getSlides();

		final int prime = 47;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result + (name != null ? name.hashCode() : 0);
		result = prime * result + (note != null ? note.hashCode() : 0);
		result = prime * result + (randomized ? 1 : 0);
		result = prime * result + slides.hashCode();
		return result;
	}

}
