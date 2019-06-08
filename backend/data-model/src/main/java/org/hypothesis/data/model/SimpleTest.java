package org.hypothesis.data.model;

import java.io.Serializable;
import java.util.Date;
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

import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.HasId;
import org.hypothesis.data.interfaces.TableConstants;

@SuppressWarnings("serial")
@Entity
@Table(name = TableConstants.TEST_TABLE)
@Access(AccessType.PROPERTY)
public class SimpleTest implements Serializable, HasId<Long> {

	private Long id;

	/**
	 * signalize if test data are for production
	 */
	private boolean production;

	/**
	 * timestamp test created at
	 */
	private Date created;

	/**
	 * timestamp test started at
	 */
	private Date started;

	/**
	 * timestamp test done at
	 */
	private Date finished;

	/**
	 * timestamp test broken at
	 */
	private Date broken;

	/**
	 * timestamp of last access
	 */
	private Date lastAccess;

	/**
	 * status code
	 */
	private Integer status;

	private Long userId;

	private long packId;

	/**
	 * last processing branch
	 */
	private Long lastBranchId;

	/**
	 * last processing task
	 */
	private Long lastTaskId;

	/**
	 * last processing slide
	 */
	private Long lastSlideId;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.TEST_GENERATOR)
	@SequenceGenerator(name = TableConstants.TEST_GENERATOR, sequenceName = TableConstants.TEST_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = FieldConstants.PRODUCTION, nullable = false)
	public boolean isProduction() {
		return production;
	}

	public void setProduction(boolean production) {
		this.production = production;
	}

	@Column(name = FieldConstants.CREATED, nullable = false)
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Column(name = FieldConstants.STARTED)
	public Date getStarted() {
		return started;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

	@Column(name = FieldConstants.FINISHED)
	public Date getFinished() {
		return finished;
	}

	public void setFinished(Date finished) {
		this.finished = finished;
	}

	@Column(name = FieldConstants.BROKEN)
	public Date getBroken() {
		return broken;
	}

	public void setBroken(Date broken) {
		this.broken = broken;
	}

	@Column(name = FieldConstants.LAST_ACCESS, nullable = false)
	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}

	@Column(name = FieldConstants.STATUS, nullable = false)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = FieldConstants.USER_ID)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = FieldConstants.PACK_ID, nullable = false)
	public long getPackId() {
		return packId;
	}

	public void setPackId(long packId) {
		this.packId = packId;
	}

	@Column(name = FieldConstants.LAST_BRANCH_ID)
	public Long getLastBranchId() {
		return lastBranchId;
	}

	public void setLastBranchId(Long branchId) {
		this.lastBranchId = branchId;
	}

	@Column(name = FieldConstants.LAST_TASK_ID)
	public Long getLastTaskId() {
		return lastTaskId;
	}

	public void setLastTaskId(Long taskId) {
		this.lastTaskId = taskId;
	}

	@Column(name = FieldConstants.LAST_SLIDE_ID)
	public Long getLastSlideId() {
		return lastSlideId;
	}

	public void setLastSlideId(Long slideId) {
		this.lastSlideId = slideId;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SimpleTest == false)
			return false;

		final SimpleTest other = (SimpleTest) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(isProduction(), other.isProduction()))
			return false;
		if (!Objects.equals(getCreated(), other.getCreated()))
			return false;
		if (!Objects.equals(getStarted(), other.getStarted()))
			return false;
		if (!Objects.equals(getFinished(), other.getFinished()))
			return false;
		if (!Objects.equals(getBroken(), other.getBroken()))
			return false;
		if (!Objects.equals(getLastAccess(), other.getLastAccess()))
			return false;
		if (!Objects.equals(getStatus(), other.getStatus()))
			return false;
		if (!Objects.equals(getUserId(), other.getUserId()))
			return false;
		if (!Objects.equals(getPackId(), other.getPackId()))
			return false;
		if (!Objects.equals(getLastBranchId(), other.getLastBranchId()))
			return false;
		if (!Objects.equals(getLastTaskId(), other.getLastTaskId()))
			return false;
		if (!Objects.equals(getLastSlideId(), other.getLastSlideId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimpleTest [id=" + id + ", production=" + production + ", created=" + created + ", started=" + started
				+ ", finished=" + finished + ", broken=" + broken + ", lastAccess=" + lastAccess + ", status=" + status
				+ ", userId=" + userId + ", packId=" + packId + ", lastBranchId=" + lastBranchId + ", lastTaskId="
				+ lastTaskId + ", lastSlideId=" + lastSlideId + "]";
	}

}
