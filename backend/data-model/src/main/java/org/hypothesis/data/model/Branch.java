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
@Table(name = TableConstants.BRANCH_TABLE)
@Access(AccessType.PROPERTY)
public final class Branch extends SerializableIdObject implements HasList<Task> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 784492179687812238L;

	private String note;

	/**
	 * serialized data for branch
	 */
	private String data;

	/**
	 * list of tasks
	 */
	private List<Task> tasks = new LinkedList<Task>();

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.BRANCH_GENERATOR)
	@SequenceGenerator(name = TableConstants.BRANCH_GENERATOR, sequenceName = TableConstants.BRANCH_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return super.getId();
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
	@JoinTable(name = TableConstants.BRANCH_TASK_TABLE, joinColumns = @JoinColumn(name = FieldConstants.BRANCH_ID) , inverseJoinColumns = @JoinColumn(name = FieldConstants.TASK_ID) )
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.TRUE)
	@OrderColumn(name = FieldConstants.RANK)
	public List<Task> getTasks() {
		return tasks;
	}

	protected void setTasks(List<Task> list) {
		this.tasks = list;
	}

	@Transient
	public final List<Task> getList() {
		return getTasks();
	}

	public final void addTask(Task task) {
		if (task != null) {
			getTasks().add(task);
		}
	}

	public final void removeTask(Task task) {
		getTasks().remove(task);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Branch)) {
			return false;
		}
		Branch other = (Branch) obj;

		Long id = getId();
		Long id2 = other.getId();
		String note = getNote();
		String note2 = other.getNote();
		String xmlData = getData();
		String xmlData2 = other.getData();

		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (note == null) {
			if (note2 != null) {
				return false;
			}
		} else if (!note.equals(note2)) {
			return false;
		}

		if (xmlData == null) {
			if (xmlData2 != null) {
				return false;
			}
		} else if (!xmlData.equals(xmlData2)) {
			return false;
		}

		if (!getTasks().equals(other.getTasks())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		Long id = getId();
		String note = getNote();
		String xmlData = getData();

		final int prime = 3;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result + (note != null ? note.hashCode() : 0);
		result = prime * result + (xmlData != null ? xmlData.hashCode() : 0);
		result = prime * result + getTasks().hashCode();

		return result;
	}

}
