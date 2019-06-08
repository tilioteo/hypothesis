/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;
import org.hibernate.annotations.Type;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.HasId;
import org.hypothesis.data.interfaces.TableConstants;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Entity
@Subselect("SELECT e." + FieldConstants.ID + //
		",e." + FieldConstants.TIMESTAMP + //
		",e." + FieldConstants.CLIENT_TIMESTAMP + //
		",e." + FieldConstants.TYPE + //
		",e." + FieldConstants.NAME + //
		",e." + FieldConstants.XML_DATA + //
		",e." + FieldConstants.BRANCH_ID + //
		",e." + FieldConstants.TASK_ID + //
		",e." + FieldConstants.SLIDE_ID + //
		",t." + FieldConstants.ID + " " + FieldConstants.TEST_ID + //
		",t." + FieldConstants.USER_ID + //
		",t." + FieldConstants.PACK_ID + //
		",t." + FieldConstants.CREATED + //
		",p." + FieldConstants.NAME + " " + FieldConstants.PACK_NAME + //
		",b." + FieldConstants.NOTE + " " + FieldConstants.BRANCH_NAME + //
		",ta." + FieldConstants.NAME + " " + FieldConstants.TASK_NAME + //
		",s." + FieldConstants.NOTE + " " + FieldConstants.SLIDE_NAME + //
		" FROM " + TableConstants.EVENT_TABLE + " e JOIN " + //
		TableConstants.TEST_EVENT_TABLE + " te ON te." + FieldConstants.EVENT_ID + "=e." + FieldConstants.ID + " JOIN "
		+ //
		TableConstants.TEST_TABLE + " t ON te." + FieldConstants.TEST_ID + "=t." + FieldConstants.ID + " LEFT JOIN " + //
		TableConstants.PACK_TABLE + " p ON t." + FieldConstants.PACK_ID + "=p." + FieldConstants.ID + " LEFT JOIN " + //
		TableConstants.BRANCH_TABLE + " b ON e." + FieldConstants.BRANCH_ID + "=b." + FieldConstants.ID + " LEFT JOIN "
		+ //
		TableConstants.TASK_TABLE + " ta ON e." + FieldConstants.TASK_ID + "=ta." + FieldConstants.ID + " LEFT JOIN " + //
		TableConstants.SLIDE_TABLE + " s ON e." + FieldConstants.SLIDE_ID + "=s." + FieldConstants.ID)
@Synchronize({ TableConstants.EVENT_TABLE, TableConstants.TEST_TABLE, TableConstants.TEST_EVENT_TABLE,
		TableConstants.PACK_TABLE, TableConstants.BRANCH_TABLE, TableConstants.TASK_TABLE, TableConstants.SLIDE_TABLE })
@Immutable
@Access(AccessType.PROPERTY)
public class ExportEvent implements Serializable, HasId<Long> {

	private long id;

	/**
	 * timestamp of event
	 */
	private long timeStamp;

	/**
	 * client timestamp of event (if possible)
	 */
	private Long clientTimeStamp;

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
	private String data;

	/**
	 * processed branch id
	 */
	private Long branchId;

	/**
	 * processed branch name
	 */
	private String branchName;

	/**
	 * processed task id
	 */
	private Long taskId;

	/**
	 * processed task name
	 */
	private String taskName;

	/**
	 * processed slide id
	 */
	private Long slideId;

	/**
	 * processed slide name
	 */
	private String slideName;

	/**
	 * current processing test id
	 */
	private Long testId;

	/**
	 * current processing user id
	 */
	private Long userId;

	/**
	 * current processing pack id
	 */
	private Long packId;

	/**
	 * current processing pack name
	 */
	private String packName;

	/**
	 * timestamp test created at
	 */
	private Date created;

	@Override
	@Id
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return id;
	}

	protected void setId(long id) {
		this.id = id;
	}

	@Column(name = FieldConstants.TIMESTAMP, nullable = false)
	public long getTimeStamp() {
		return timeStamp;
	}

	protected void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Column(name = FieldConstants.CLIENT_TIMESTAMP)
	public Long getClientTimeStamp() {
		return clientTimeStamp;
	}

	protected void setClientTimeStamp(Long clientTimeStamp) {
		this.clientTimeStamp = clientTimeStamp;
	}

	@Column(name = FieldConstants.TYPE, nullable = false)
	public Long getType() {
		return type;
	}

	protected void setType(Long type) {
		this.type = type;
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

	protected void setData(String data) {
		this.data = data;
	}

	@Column(name = FieldConstants.BRANCH_ID)
	public Long getBranchId() {
		return branchId;
	}

	protected void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	@Column(name = FieldConstants.BRANCH_NAME)
	public String getBranchName() {
		return branchName;
	}

	protected void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	@Column(name = FieldConstants.TASK_ID)
	public Long getTaskId() {
		return taskId;
	}

	protected void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	@Column(name = FieldConstants.TASK_NAME)
	public String getTaskName() {
		return taskName;
	}

	protected void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	@Column(name = FieldConstants.SLIDE_ID)
	public Long getSlideId() {
		return slideId;
	}

	protected void setSlideId(Long slideId) {
		this.slideId = slideId;
	}

	@Column(name = FieldConstants.SLIDE_NAME)
	public String getSlideName() {
		return slideName;
	}

	protected void setSlideName(String slideName) {
		this.slideName = slideName;
	}

	@Column(name = FieldConstants.TEST_ID)
	public Long getTestId() {
		return testId;
	}

	protected void setTestId(Long testId) {
		this.testId = testId;
	}

	@Column(name = FieldConstants.USER_ID)
	public Long getUserId() {
		return userId;
	}

	protected void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = FieldConstants.PACK_ID)
	public Long getPackId() {
		return packId;
	}

	protected void setPackId(Long packId) {
		this.packId = packId;
	}

	@Column(name = FieldConstants.PACK_NAME)
	public String getPackName() {
		return packName;
	}

	protected void setPackName(String packName) {
		this.packName = packName;
	}

	@Column(name = FieldConstants.CREATED, nullable = false)
	public Date getCreated() {
		return created;
	}

	protected void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExportEvent == false)
			return false;

		final ExportEvent other = (ExportEvent) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(getTimeStamp(), other.getTimeStamp()))
			return false;
		if (!Objects.equals(getClientTimeStamp(), other.getClientTimeStamp()))
			return false;
		if (!Objects.equals(getType(), other.getType()))
			return false;
		if (!Objects.equals(getName(), other.getName()))
			return false;
		if (!Objects.equals(getData(), other.getData()))
			return false;
		if (!Objects.equals(getBranchId(), other.getBranchId()))
			return false;
		if (!Objects.equals(getBranchName(), other.getBranchName()))
			return false;
		if (!Objects.equals(getTaskId(), other.getTaskId()))
			return false;
		if (!Objects.equals(getTaskName(), other.getTaskName()))
			return false;
		if (!Objects.equals(getSlideId(), other.getSlideId()))
			return false;
		if (!Objects.equals(getSlideName(), other.getSlideName()))
			return false;
		if (!Objects.equals(getTestId(), other.getTestId()))
			return false;
		if (!Objects.equals(getUserId(), other.getUserId()))
			return false;
		if (!Objects.equals(getPackId(), other.getPackId()))
			return false;
		if (!Objects.equals(getPackName(), other.getPackName()))
			return false;
		if (!Objects.equals(getCreated(), other.getCreated()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExportEvent [id=" + id + ", timeStamp=" + timeStamp + ", clientTimeStamp=" + clientTimeStamp + ", type="
				+ type + ", name=" + name + ", branchId=" + branchId + ", branchName=" + branchName + ", taskId="
				+ taskId + ", taskName=" + taskName + ", slideId=" + slideId + ", slideName=" + slideName + ", testId="
				+ testId + ", userId=" + userId + ", packId=" + packId + ", packName=" + packName + ", created="
				+ created + ", data=" + data + "]";
	}

}
