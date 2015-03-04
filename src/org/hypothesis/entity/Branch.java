/**
 * 
 */
package org.hypothesis.entity;

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

import org.dom4j.Document;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.hypothesis.application.common.xml.BranchXmlConstants;
import org.hypothesis.common.HasQueue;
import org.hypothesis.common.SerializableIdObject;
import org.hypothesis.common.xml.Utility;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for branch
 * 
 */
@Entity
@Table(name = "TBL_BRANCH")
@Access(AccessType.PROPERTY)
public final class Branch extends SerializableIdObject implements HasQueue<Task> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 784492179687812238L;

 	private String note;

	/**
	 * raw xml string for branch
	 */
	private String xmlData;

	/**
	 * list of tasks
	 */
	private List<Task> tasks = new LinkedList<Task>();

	/**
	 * parsed dom document from xml
	 */
	private transient Document document = null;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "branchGenerator")
	@SequenceGenerator(name = "branchGenerator", sequenceName = "hbn_branch_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	public final Long getId() {
		return super.getId();
	}

	@Column(name = "NOTE")
	public final String getNote() {
		return note;
	}

	public final void setNote(String note) {
		this.note = note;
	}

	@Column(name = "XML_DATA", nullable = false)
	@Type(type="text")
	protected String getXmlData() {
		return xmlData;
	}

	protected void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "TBL_BRANCH_TASK", joinColumns = @JoinColumn(name = "BRANCH_ID"), inverseJoinColumns = @JoinColumn(name = "TASK_ID"))
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@OrderColumn(name = "RANK")
	public final List<Task> getTasks() {
		return tasks;
	}

	protected void setTasks(List<Task> list) {
		this.tasks = list;
	}

	@Transient
	public final Document getDocument() {
		if (document == null) {
			document = Utility.readString(getXmlData());
		}
		return document;
	}

	public final void setDocument(Document document) {
		if (document != getDocument()) {
			if (isValidDocument(document)) {
				this.document = document;
				this.xmlData = Utility.writeString(this.document);
			} else {
				this.document = null;
				this.xmlData = null;
				// throw new InvalidBranchXmlException();
			}
		}
	}

	@Transient
	public final List<Task> getQueue() {
		return tasks;
	}

	public final void addTask(Task task) {
		if (task != null)
			this.tasks.add(task);
	}

	public final void removeTask(Task task) {
		this.tasks.remove(task);
	}

	private boolean isValidDocument(Document doc) {
		return (doc != null && doc.getRootElement() != null && doc
				.getRootElement().getName().equals(BranchXmlConstants.BRANCH));
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Branch))
			return false;
		Branch other = (Branch) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		// TODO remove when Buffered.SourceException occurs
		if (getNote() == null) {
			if (other.getNote() != null)
				return false;
		} else if (!getNote().equals(other.getNote()))
			return false;
		if (getTasks() == null) {
			if (other.getTasks() != null)
				return false;
		} else if (!getTasks().equals(other.getTasks()))
			return false;
		return true;
	}

	@Override
	public final int hashCode() {
		final int prime = 61;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		// TODO remove when Buffered.SourceException occurs
		result = prime * result
				+ ((getNote() == null) ? 0 : getNote().hashCode());
		result = prime * result
				+ ((getTasks() == null) ? 0 : getTasks().hashCode());
		return result;
	}

}
