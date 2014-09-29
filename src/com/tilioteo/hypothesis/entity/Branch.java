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

import org.dom4j.Document;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.common.EntityTableConstants;
import com.tilioteo.hypothesis.dom.BranchXmlConstants;
import com.tilioteo.hypothesis.dom.XmlUtility;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for branch
 * 
 */
@Entity
@Table(name = EntityTableConstants.BRANCH_TABLE)
@Access(AccessType.PROPERTY)
public final class Branch extends SerializableIdObject implements HasList<Task> {

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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EntityTableConstants.BRANCH_GENERATOR)
	@SequenceGenerator(name = EntityTableConstants.BRANCH_GENERATOR, sequenceName = EntityTableConstants.BRANCH_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = EntityFieldConstants.ID)
	public final Long getId() {
		return super.getId();
	}

	@Column(name = EntityFieldConstants.NOTE)
	public final String getNote() {
		return note;
	}

	public final void setNote(String note) {
		this.note = note;
	}

	@Column(name = EntityFieldConstants.XML_DATA, nullable = false)
	@Type(type="text")
	protected String getXmlData() {
		return xmlData;
	}

	protected void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = EntityTableConstants.BRANCH_TASK_TABLE, joinColumns = @JoinColumn(name = EntityFieldConstants.BRANCH_ID), inverseJoinColumns = @JoinColumn(name = EntityFieldConstants.TASK_ID))
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.TRUE)
	@OrderColumn(name = EntityFieldConstants.RANK)
	public List<Task> getTasks() {
		return tasks;
	}

	protected void setTasks(List<Task> list) {
		this.tasks = list;
	}

	@Transient
	public final Document getDocument() {
		if (document == null) {
			document = XmlUtility.readString(getXmlData());
		}
		return document;
	}

	public final void setDocument(Document document) {
		if (document != getDocument()) {
			if (isValidDocument(document)) {
				this.document = document;
				this.xmlData = XmlUtility.writeString(this.document);
			} else {
				this.document = null;
				this.xmlData = null;
				// throw new InvalidBranchXmlException();
			}
		}
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

	private boolean isValidDocument(Document doc) {
		return (doc != null && doc.getRootElement() != null && doc
				.getRootElement().getName().equals(BranchXmlConstants.BRANCH));
	}

	@Override
	public final boolean equals(Object obj) {
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
		String xmlData = getXmlData();
		String xmlData2 = other.getXmlData();
		
		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (note != null && !note.equals(note2)) {
			return false;
		} else if (note2 != null) {
			return false;
		}
		
		if (xmlData != null && !xmlData.equals(xmlData2)) {
			return false;
		} else if (xmlData2 != null) {
			return false;
		}
		
		if (!getTasks().equals(other.getTasks())) {
			return false;
		}
		
		return true;
	}

	@Override
	public final int hashCode() {
		Long id = getId();
		String note = getNote();
		String xmlData = getXmlData();
		
		final int prime = 3;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result	+ (note != null ? note.hashCode() : 0);
		result = prime * result	+ (xmlData != null ? xmlData.hashCode() : 0);
		result = prime * result + getTasks().hashCode();
		
		return result;
	}

}
