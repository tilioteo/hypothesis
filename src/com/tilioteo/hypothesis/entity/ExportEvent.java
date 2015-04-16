/**
 * 
 */
package com.tilioteo.hypothesis.entity;

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
 * @author kamil
 *
 */
@Entity
@Subselect("SELECT e.ID,e.TIMESTAMP,e.TYPE,e.NAME,e.XML_DATA,e.BRANCH_ID,e.TASK_ID,e.SLIDE_ID,t.ID TEST_ID,t.USER_ID,t.PACK_ID,t.CREATED FROM TBL_EVENT e,TBL_TEST t,TBL_TEST_EVENT te WHERE te.EVENT_ID=e.ID AND te.TEST_ID=t.ID")
@Synchronize({TableConstants.EVENT_TABLE, TableConstants.TEST_TABLE, TableConstants.TEST_EVENT_TABLE })
@Immutable
@Access(AccessType.PROPERTY)
public class ExportEvent extends SerializableIdObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4342703994277025881L;

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
	 * current processing branch id
	 */
	private Long branchId;

	/**
	 * current processing task id
	 */
	private Long taskId;

	/**
	 * current processing slide id
	 */
	private Long slideId;

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
	@Type(type="text")
	public String getXmlData() {
		return xmlData;
	}

	protected void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}

	@Column(name = FieldConstants.BRANCH_ID)
	public Long getBranchId() {
		return branchId;
	}

	protected void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	@Column(name = FieldConstants.TASK_ID)
	public Long getTaskId() {
		return taskId;
	}

	protected void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	@Column(name = FieldConstants.SLIDE_ID)
	public Long getSlideId() {
		return slideId;
	}

	protected void setSlideId(Long slideId) {
		this.slideId = slideId;
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
