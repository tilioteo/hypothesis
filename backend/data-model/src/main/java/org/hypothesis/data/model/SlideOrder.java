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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.HasId;
import org.hypothesis.data.interfaces.TableConstants;

@SuppressWarnings({ "serial", "deprecation" })
@Entity
@Table(name = TableConstants.SLIDE_ORDER_TABLE)
@org.hibernate.annotations.Table(appliesTo = TableConstants.SLIDE_ORDER_TABLE, indexes = {
		@Index(name = "IX_TEST_TASK", columnNames = { FieldConstants.TEST_ID, FieldConstants.TASK_ID }) })
@Access(AccessType.PROPERTY)
public class SlideOrder implements Serializable, HasId<Long> {

	private Long id;

	/**
	 * processing test
	 */
	private long testId;

	private long taskId;

	private String data;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.SLIDE_ORDER_GENERATOR)
	@SequenceGenerator(name = TableConstants.SLIDE_ORDER_GENERATOR, sequenceName = TableConstants.SLIDE_ORDER_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = FieldConstants.TEST_ID, nullable = false)
	public long getTestId() {
		return testId;
	}

	public void setTestId(long testId) {
		this.testId = testId;
	}

	@Column(name = FieldConstants.TASK_ID, nullable = false)
	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	@Column(name = FieldConstants.XML_DATA)
	@Type(type = "text")
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SlideOrder == false)
			return false;

		final SlideOrder other = (SlideOrder) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(getTestId(), other.getTestId()))
			return false;
		if (!Objects.equals(getTaskId(), other.getTaskId()))
			return false;
		if (!Objects.equals(getData(), other.getData()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SlideOrder [id=" + id + ", testId=" + testId + ", taskId=" + taskId + ", data=" + data + "]";
	}

}
