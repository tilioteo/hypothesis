/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
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

	@ManyToOne
	@JoinColumn(name = EntityFieldConstants.BRANCH_ID)
	public final Branch getBranch() {
		return branch;
	}

	public final void setBranch(Branch branch) {
		this.branch = branch;
	}

	@ManyToOne
	@JoinColumn(name = EntityFieldConstants.TASK_ID)
	public final Task getTask() {
		return task;
	}

	public final void setTask(Task task) {
		this.task = task;
	}

	@ManyToOne
	@JoinColumn(name = EntityFieldConstants.SLIDE_ID)
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Event)) {
			return false;
		}
		Event other = (Event) obj;

		Long id = getId();
		Long id2 = other.getId();
		Long timeStamp = getTimeStamp();
		Long timeStamp2 = other.getTimeStamp();
		Long type = getType();
		Long type2 = other.getType();
		String name = getName();
		String name2 = other.getName();
		String xmlData = getXmlData();
		String xmlData2 = other.getXmlData();
		Branch branch = getBranch();
		Branch branch2 = other.getBranch();
		Task task = getTask();
		Task task2 = other.getTask();
		Slide slide = getSlide();
		Slide slide2 = other.getSlide();
		
		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (timeStamp != null && !timeStamp.equals(timeStamp2)) {
			return false;
		} else if (timeStamp2 != null) {
			return false;
		}
		
		if (type != null && !type.equals(type2)) {
			return false;
		} else if (type2 != null) {
			return false;
		}
		
		if (name != null && !name.equals(name2)) {
			return false;
		} else if (name2 != null) {
			return false;
		}
		
		if (xmlData != null && !xmlData.equals(xmlData2)) {
			return false;
		} else if (xmlData2 != null) {
			return false;
		}
		
		if (branch != null && !branch.equals(branch2)) {
			return false;
		} else if (branch2 != null) {
			return false;
		}
		
		if (task != null && !task.equals(task2)) {
			return false;
		} else if (task2 != null) {
			return false;
		}
		
		if (slide != null && !slide.equals(slide2)) {
			return false;
		} else if (slide2 != null) {
			return false;
		}
		
		return true;
	}

	@Override
	public final int hashCode() {
		Long id = getId();
		Long timeStamp = getTimeStamp();
		Long type = getType();
		String name = getName();
		String xmlData = getXmlData();
		Branch branch = getBranch();
		Task task = getTask();
		Slide slide = getSlide();

		final int prime = 11;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result	+ (timeStamp != null ? timeStamp.hashCode() : 0);
		result = prime * result	+ (type != null ? type.hashCode() : 0);
		result = prime * result	+ (name != null ? name.hashCode() : 0);
		result = prime * result	+ (xmlData != null ? xmlData.hashCode() : 0);
		result = prime * result	+ (branch != null ? branch.hashCode() : 0);
		result = prime * result	+ (task != null ? task.hashCode() : 0);
		result = prime * result	+ (slide != null ? slide.hashCode() : 0);
		return result;
	}

}
