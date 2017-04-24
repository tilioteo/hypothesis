/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.model;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;
import org.hibernate.annotations.Type;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@Entity
@Subselect("SELECT s."+FieldConstants.ID+
		",s."+FieldConstants.TIMESTAMP+
		",s."+FieldConstants.NAME+
		",s."+FieldConstants.XML_DATA+
		",s."+FieldConstants.BRANCH_ID+
		",s."+FieldConstants.TASK_ID+
		",s."+FieldConstants.SLIDE_ID+
		",t."+FieldConstants.ID+" "+FieldConstants.TEST_ID+
		",t."+FieldConstants.USER_ID+
		",t."+FieldConstants.PACK_ID+
		",t."+FieldConstants.CREATED+
		",p."+FieldConstants.NAME+" "+FieldConstants.PACK_NAME+
		",b."+FieldConstants.NOTE+" "+FieldConstants.BRANCH_NAME+
		",ta."+FieldConstants.NAME+" "+FieldConstants.TASK_NAME+
		",sl."+FieldConstants.NOTE+" "+FieldConstants.SLIDE_NAME+
		" FROM "+TableConstants.SCORE_TABLE+" s JOIN "+
		TableConstants.TEST_SCORE_TABLE+" ts ON ts."+FieldConstants.SCORE_ID+"=s."+FieldConstants.ID+" JOIN "+
		TableConstants.TEST_TABLE+" t ON ts."+FieldConstants.TEST_ID+"=t."+FieldConstants.ID+" LEFT JOIN "+
		TableConstants.PACK_TABLE+" p ON t."+FieldConstants.PACK_ID+"=p."+FieldConstants.ID+" LEFT JOIN "+
		TableConstants.BRANCH_TABLE+" b ON s."+FieldConstants.BRANCH_ID+"=b."+FieldConstants.ID+" LEFT JOIN "+
		TableConstants.TASK_TABLE+" ta ON s."+FieldConstants.TASK_ID+"=ta."+FieldConstants.ID+" LEFT JOIN "+
		TableConstants.SLIDE_TABLE+" sl ON s."+FieldConstants.SLIDE_ID+"=sl."+FieldConstants.ID)
@Synchronize({ TableConstants.SCORE_TABLE, TableConstants.TEST_TABLE, TableConstants.TEST_SCORE_TABLE,
		TableConstants.PACK_TABLE, TableConstants.BRANCH_TABLE, TableConstants.TASK_TABLE, TableConstants.SLIDE_TABLE })
@Immutable
@Access(AccessType.PROPERTY)
public class ExportScore extends SerializableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6353743831733014773L;

	/**
	 * timestamp of event
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

	@Transient
	public final Date getDatetime() {
		return new Date(getTimeStamp());
	}

}