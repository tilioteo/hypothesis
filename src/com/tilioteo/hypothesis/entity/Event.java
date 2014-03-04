/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.common.EntityTableConstants;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for test event event is every action during test
 *         running
 * 
 *         this class will change according to new application model
 * 
 */
@Entity
@Table(name = EntityTableConstants.EVENT_TABLE)
@Access(AccessType.PROPERTY)
public final class Event extends SerializableIdObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1958866075625431131L;

	/**
	 * timestamp of event
	 */
	private Long timeStamp;

	/**
	 * code of event type
	 */
	private Long type;

	/**
	 * human readable name
	 */
	private String name;

	/**
	 * saved data
	 */
	private String xmlData;

	/**
	 * event result
	 */
	/*
	 * private Integer result = null; private String resultDetail = null;
	 */

	/**
	 * current processing branch
	 */
	private Branch branch;

	/**
	 * current processing task
	 */
	private Task task;

	/**
	 * current processing slide
	 */
	private Slide slide;

	protected Event() {
		super();
	}

	public Event(long type, String name, Date datetime) {
		this();
		this.type = type;
		this.name = name;
		this.timeStamp = datetime.getTime();
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EntityTableConstants.EVENT_GENERATOR)
	@SequenceGenerator(name = EntityTableConstants.EVENT_GENERATOR, sequenceName = EntityTableConstants.EVENT_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = EntityFieldConstants.ID)
	public final Long getId() {
		return super.getId();
	}

	@Column(name = EntityFieldConstants.TIMESTAMP, nullable = false)
	protected Long getTimeStamp() {
		return timeStamp;
	}

	protected void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Column(name = EntityFieldConstants.TYPE, nullable = false)
	public final Long getType() {
		return type;
	}

	protected void setType(Long type) {
		this.type = type;
	}

	@Column(name = EntityFieldConstants.NAME)
	public final String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	@Column(name = EntityFieldConstants.XML_DATA)
	@Type(type="text")
	public final String getXmlData() {
		return xmlData;
	}

	public final void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = EntityFieldConstants.BRANCH_ID)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Branch getBranch() {
		return branch;
	}

	public final void setBranch(Branch branch) {
		this.branch = branch;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = EntityFieldConstants.TASK_ID)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Task getTask() {
		return task;
	}

	public final void setTask(Task task) {
		this.task = task;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = EntityFieldConstants.SLIDE_ID)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Slide getSlide() {
		return slide;
	}

	public final void setSlide(Slide slide) {
		this.slide = slide;
	}

	@Transient
	public final Date getDatetime() {
		return new Date(getTimeStamp());
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Event))
			return false;
		Event other = (Event) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		// TODO remove when Buffered.SourceException occurs
		if (getBranch() == null) {
			if (other.getBranch() != null)
				return false;
		} else if (!getBranch().equals(other.getBranch()))
			return false;
		if (getXmlData() == null) {
			if (other.getXmlData() != null)
				return false;
		} else if (!getXmlData().equals(other.getXmlData()))
			return false;
		/*
		 * if (getResult() == null) { if (other.getResult() != null) return
		 * false; } else if (!getResult().equals(other.getResult())) return
		 * false; if (getResultDetail() == null) { if (other.getResultDetail()
		 * != null) return false; } else if
		 * (!getResultDetail().equals(other.getResultDetail())) return false;
		 */
		if (getSlide() == null) {
			if (other.getSlide() != null)
				return false;
		} else if (!getSlide().equals(other.getSlide()))
			return false;
		if (getTask() == null) {
			if (other.getTask() != null)
				return false;
		} else if (!getTask().equals(other.getTask()))
			return false;
		if (getTimeStamp() == null) {
			if (other.getTimeStamp() != null)
				return false;
		} else if (!getTimeStamp().equals(other.getTimeStamp()))
			return false;
		if (getType() == null) {
			if (other.getType() != null)
				return false;
		} else if (!getType().equals(other.getType()))
			return false;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		return true;
	}

	@Override
	public final int hashCode() {
		final int prime = 101;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		// TODO remove when Buffered.SourceException occurs
		result = prime * result
				+ ((getBranch() == null) ? 0 : getBranch().hashCode());
		result = prime * result
				+ ((getXmlData() == null) ? 0 : getXmlData().hashCode());
		/*
		 * result = prime * result + ((getResult() == null) ? 0 :
		 * getResult().hashCode()); result = prime * result +
		 * ((getResultDetail() == null) ? 0 : getResultDetail().hashCode());
		 */
		result = prime * result
				+ ((getSlide() == null) ? 0 : getSlide().hashCode());
		result = prime * result
				+ ((getTask() == null) ? 0 : getTask().hashCode());
		result = prime * result
				+ ((getTimeStamp() == null) ? 0 : getTimeStamp().hashCode());
		result = prime * result
				+ ((getType() == null) ? 0 : getType().hashCode());
		result = prime * result
				+ ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

}
