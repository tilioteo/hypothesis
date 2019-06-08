package org.hypothesis.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.HasId;
import org.hypothesis.data.interfaces.TableConstants;

@SuppressWarnings("serial")
@Entity
@Table(name = TableConstants.SCORE_TABLE)
@SecondaryTables({ @SecondaryTable(name = TableConstants.TEST_SCORE_TABLE, pkJoinColumns = {
		@PrimaryKeyJoinColumn(name = FieldConstants.SCORE_ID, referencedColumnName = FieldConstants.ID) }) })
@Access(AccessType.PROPERTY)
public class Score implements Serializable, HasId<Long> {

	private Long id;

	/**
	 * server timestamp of event
	 */
	private long timeStamp;

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
	private Long branchId;

	/**
	 * current processing task
	 */
	private Long taskId;

	/**
	 * current processing slide
	 */
	private Long slideId;

	/**
	 * linked test
	 */
	private Long testId;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.SCORE_GENERATOR)
	@SequenceGenerator(name = TableConstants.SCORE_GENERATOR, sequenceName = TableConstants.SCORE_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = FieldConstants.TIMESTAMP, nullable = false)
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Column(name = FieldConstants.NAME)
	public String getName() {
		return name;
	}

	public void setName(String name) {
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

	@Column(name = FieldConstants.BRANCH_ID)
	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	@Column(name = FieldConstants.TASK_ID)
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	@Column(name = FieldConstants.SLIDE_ID)
	public Long getSlideId() {
		return slideId;
	}

	public void setSlideId(Long slideId) {
		this.slideId = slideId;
	}

	@Column(name = FieldConstants.TEST_ID, table = TableConstants.TEST_SCORE_TABLE, insertable = false, updatable = false)
	public Long getTestId() {
		return testId;
	}

	public void setTestId(Long testId) {
		this.testId = testId;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Score == false)
			return false;

		final Score other = (Score) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(getTimeStamp(), other.getTimeStamp()))
			return false;
		if (!Objects.equals(getName(), other.getName()))
			return false;
		if (!Objects.equals(getData(), other.getData()))
			return false;
		if (!Objects.equals(getBranchId(), other.getBranchId()))
			return false;
		if (!Objects.equals(getTaskId(), other.getTaskId()))
			return false;
		if (!Objects.equals(getSlideId(), other.getSlideId()))
			return false;
		if (!Objects.equals(getTestId(), other.getTestId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Score [id=" + id + ", timeStamp=" + timeStamp + ", name=" + name + ", branchId=" + branchId
				+ ", taskId=" + taskId + ", slideId=" + slideId + ", testId=" + testId + ", data=" + data + "]";
	}

}
