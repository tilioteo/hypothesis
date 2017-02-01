/**
 * 
 */
package org.hypothesis.data.model;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 *         Database entity for score recorded during test
 * 
 */
@Entity
@Table(name = TableConstants.SCORE_TABLE)
@Access(AccessType.PROPERTY)
public class Score extends SerializableIdObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4734568201226095583L;

	/**
	 * server timestamp of event
	 */
	private Long timeStamp;

	/**
	 * event or action name
	 */
	private String name;

	/**
	 * saved data
	 */
	private String data;

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

	/**
	 * linked test
	 */
	private Test test;

	protected Score() {
		super();
	}

	public Score(Date datetime, String data) {
		this();
		this.timeStamp = datetime != null ? datetime.getTime() : null;
		this.data = data;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.SCORE_GENERATOR)
	@SequenceGenerator(name = TableConstants.SCORE_GENERATOR, sequenceName = TableConstants.SCORE_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return super.getId();
	}

	@Column(name = FieldConstants.TIMESTAMP, nullable = false)
	protected Long getTimeStamp() {
		return timeStamp;
	}

	protected void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Column(name = FieldConstants.NAME)
	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	@Column(name = FieldConstants.XML_DATA)
	@Type(type = "text")
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@ManyToOne
	@JoinColumn(name = FieldConstants.BRANCH_ID)
	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	@ManyToOne
	@JoinColumn(name = FieldConstants.TASK_ID)
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	@ManyToOne
	@JoinColumn(name = FieldConstants.SLIDE_ID)
	public Slide getSlide() {
		return slide;
	}

	public void setSlide(Slide slide) {
		this.slide = slide;
	}

	@ManyToOne
	@JoinTable(name = TableConstants.TEST_SCORE_TABLE, joinColumns = {
			@JoinColumn(name = FieldConstants.SCORE_ID, insertable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = FieldConstants.TEST_ID, insertable = false, updatable = false) })
	public Test getTest() {
		return test;
	}

	protected void setTest(Test test) {
		this.test = test;
	}

	@Transient
	public final Date getDatetime() {
		return new Date(getTimeStamp());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Event)) {
			return false;
		}
		Score other = (Score) obj;

		Long id = getId();
		Long id2 = other.getId();
		Long timeStamp = getTimeStamp();
		Long timeStamp2 = other.getTimeStamp();
		String name = getName();
		String name2 = other.getName();
		String xmlData = getData();
		String xmlData2 = other.getData();
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

		if (timeStamp == null) {
			if (timeStamp2 != null) {
				return false;
			}
		} else if (!timeStamp.equals(timeStamp2)) {
			return false;
		}

		if (name == null) {
			if (name2 != null) {
				return false;
			}
		} else if (!name.equals(name2)) {
			return false;
		}

		if (xmlData == null) {
			if (xmlData2 != null) {
				return false;
			}
		} else if (!xmlData.equals(xmlData2)) {
			return false;
		}

		if (branch == null) {
			if (branch2 != null) {
				return false;
			}
		} else if (!branch.equals(branch2)) {
			return false;
		}

		if (task == null) {
			if (task2 != null) {
				return false;
			}
		} else if (!task.equals(task2)) {
			return false;
		}

		if (slide == null) {
			if (slide2 != null) {
				return false;
			}
		} else if (!slide.equals(slide2)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		Long id = getId();
		Long timeStamp = getTimeStamp();
		String name = getName();
		String xmlData = getData();
		Branch branch = getBranch();
		Task task = getTask();
		Slide slide = getSlide();

		final int prime = 11;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result + (timeStamp != null ? timeStamp.hashCode() : 0);
		result = prime * result + (name != null ? name.hashCode() : 0);
		result = prime * result + (xmlData != null ? xmlData.hashCode() : 0);
		result = prime * result + (branch != null ? branch.hashCode() : 0);
		result = prime * result + (task != null ? task.hashCode() : 0);
		result = prime * result + (slide != null ? slide.hashCode() : 0);
		return result;
	}

}
