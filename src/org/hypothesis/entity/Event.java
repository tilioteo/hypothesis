/**
 * 
 */
package org.hypothesis.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hypothesis.common.SerializableIdObject;

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
@Table(name = "TBL_EVENT")
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
	private Integer type;

	/**
	 * human readable name
	 */
	private String name;

	/**
	 * saved data
	 */
	private String data;

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

	public Event(int type, String name, Date datetime) {
		this();
		this.type = type;
		this.name = name;
		this.timeStamp = datetime.getTime();
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
		if (getData() == null) {
			if (other.getData() != null)
				return false;
		} else if (!getData().equals(other.getData()))
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

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "BRANCH_ID")
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Branch getBranch() {
		return branch;
	}

	@Lob
	@Column(name = "DATA")
	public final String getData() {
		return data;
	}

	@Transient
	public final Date getDatetime() {
		return new Date(getTimeStamp());
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventGenerator")
	@SequenceGenerator(name = "eventGenerator", sequenceName = "hbn_event_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	public final Long getId() {
		return super.getId();
	}

	@Column(name = "NAME")
	public final String getName() {
		return name;
	}

	/*
	 * @Column(name="RESULT") public final Integer getResult() { return result;
	 * }
	 * 
	 * public final void setResult(Integer result) { this.result = result; }
	 * 
	 * @Column(name="RESULT_DETAIL") public final String getResultDetail() {
	 * return resultDetail; }
	 * 
	 * public final void setResultDetail(String resultDetail) {
	 * this.resultDetail = resultDetail; }
	 */

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "SLIDE_ID")
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Slide getSlide() {
		return slide;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "TASK_ID")
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Task getTask() {
		return task;
	}

	@Column(name = "TIMESTAMP", nullable = false)
	protected Long getTimeStamp() {
		return timeStamp;
	}

	@Column(name = "TYPE", nullable = false)
	public final Integer getType() {
		return type;
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
				+ ((getData() == null) ? 0 : getData().hashCode());
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

	public final void setBranch(Branch branch) {
		this.branch = branch;
	}

	public final void setData(String data) {
		this.data = data;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public final void setSlide(Slide slide) {
		this.slide = slide;
	}

	public final void setTask(Task task) {
		this.task = task;
	}

	protected void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

	protected void setType(Integer type) {
		this.type = type;
	}
}
