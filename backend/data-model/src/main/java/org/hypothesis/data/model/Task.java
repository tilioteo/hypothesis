package org.hypothesis.data.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.HasId;
import org.hypothesis.data.interfaces.TableConstants;

@SuppressWarnings("serial")
@Entity
@Table(name = TableConstants.TASK_TABLE)
@Access(AccessType.PROPERTY)
public class Task implements Serializable, HasId<Long> {

	private Long id;

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
	private List<Slide> slides;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.TASK_GENERATOR)
	@SequenceGenerator(name = TableConstants.TASK_GENERATOR, sequenceName = TableConstants.TASK_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	@JoinTable(name = TableConstants.TASK_SLIDE_TABLE, joinColumns = @JoinColumn(name = FieldConstants.TASK_ID), inverseJoinColumns = @JoinColumn(name = FieldConstants.SLIDE_ID))
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.TRUE)
	@OrderColumn(name = FieldConstants.RANK)
	public List<Slide> getSlides() {
		return slides;
	}

	public void setSlides(List<Slide> list) {
		this.slides = list;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Task == false)
			return false;

		final Task other = (Task) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(getName(), other.getName()))
			return false;
		if (!Objects.equals(getNote(), other.getNote()))
			return false;
		if (!Objects.equals(getData(), other.getData()))
			return false;
		if (!Objects.equals(isRandomized(), other.isRandomized()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", name=" + name + ", note=" + note + ", randomized=" + randomized + ", data=" + data
				+ "]";
	}

}
