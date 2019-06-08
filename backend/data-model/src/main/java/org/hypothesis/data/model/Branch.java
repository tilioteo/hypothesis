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
@Table(name = TableConstants.BRANCH_TABLE)
@Access(AccessType.PROPERTY)
public class Branch implements Serializable, HasId<Long> {

	private Long id;

	private String note;

	/**
	 * serialized data for branch
	 */
	private String data;

	/**
	 * list of tasks
	 */
	private List<Task> tasks;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.BRANCH_GENERATOR)
	@SequenceGenerator(name = TableConstants.BRANCH_GENERATOR, sequenceName = TableConstants.BRANCH_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = FieldConstants.NOTE)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Column(name = FieldConstants.XML_DATA, nullable = false)
	@Type(type = "text")
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = TableConstants.BRANCH_TASK_TABLE, joinColumns = @JoinColumn(name = FieldConstants.BRANCH_ID), inverseJoinColumns = @JoinColumn(name = FieldConstants.TASK_ID))
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.TRUE)
	@OrderColumn(name = FieldConstants.RANK)
	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> list) {
		this.tasks = list;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Branch == false)
			return false;

		final Branch other = (Branch) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(getNote(), other.getNote()))
			return false;
		if (!Objects.equals(getData(), other.getData()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Branch [id=" + id + ", note=" + note + ", data=" + data + "]";
	}

}
