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

@SuppressWarnings("serial")
@Entity
@Subselect("SELECT s." + FieldConstants.ID + //
		",s." + FieldConstants.TIMESTAMP + //
		",s." + FieldConstants.NAME + //
		",s." + FieldConstants.XML_DATA + //
		",s." + FieldConstants.BRANCH_ID + //
		",s." + FieldConstants.TASK_ID + //
		",s." + FieldConstants.SLIDE_ID + //
		",t." + FieldConstants.ID + " " + FieldConstants.TEST_ID + //
		",t." + FieldConstants.USER_ID + //
		",t." + FieldConstants.PACK_ID + //
		",t." + FieldConstants.CREATED + //
		",p." + FieldConstants.NAME + " " + FieldConstants.PACK_NAME + //
		",b." + FieldConstants.NOTE + " " + FieldConstants.BRANCH_NAME + //
		",ta." + FieldConstants.NAME + " " + FieldConstants.TASK_NAME + //
		",sl." + FieldConstants.NOTE + " " + FieldConstants.SLIDE_NAME + //
		",u." + FieldConstants.USERNAME + //
		",u." + FieldConstants.PASSWORD + //
		",u." + FieldConstants.NAME + " " + FieldConstants.FIRST_NAME + //
		",u." + FieldConstants.GENDER + //
		",u." + FieldConstants.EDUCATION + //
		",u." + FieldConstants.BIRTH_DATE + //
		",u." + FieldConstants.NOTE + //
		" FROM " + TableConstants.SCORE_TABLE + " s JOIN " + //
		TableConstants.TEST_SCORE_TABLE + " ts ON ts." + FieldConstants.SCORE_ID + "=s." + FieldConstants.ID + " JOIN "
		+ //
		TableConstants.TEST_TABLE + " t ON ts." + FieldConstants.TEST_ID + "=t." + FieldConstants.ID + " LEFT JOIN " + //
		TableConstants.PACK_TABLE + " p ON t." + FieldConstants.PACK_ID + "=p." + FieldConstants.ID + " LEFT JOIN " + //
		TableConstants.BRANCH_TABLE + " b ON s." + FieldConstants.BRANCH_ID + "=b." + FieldConstants.ID + " LEFT JOIN "
		+ //
		TableConstants.TASK_TABLE + " ta ON s." + FieldConstants.TASK_ID + "=ta." + FieldConstants.ID + " LEFT JOIN " + //
		TableConstants.SLIDE_TABLE + " sl ON s." + FieldConstants.SLIDE_ID + "=sl." + FieldConstants.ID + " LEFT JOIN "
		+ //
		TableConstants.USER_TABLE + " u ON t." + FieldConstants.USER_ID + "=u." + FieldConstants.ID) //
@Synchronize({ TableConstants.SCORE_TABLE, TableConstants.TEST_TABLE, TableConstants.TEST_SCORE_TABLE,
		TableConstants.PACK_TABLE, TableConstants.BRANCH_TABLE, TableConstants.TASK_TABLE, TableConstants.SLIDE_TABLE,
		TableConstants.USER_TABLE })
@Immutable
@Access(AccessType.PROPERTY)
public class ExportScore implements Serializable, HasId<Long> {

	private long id;

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

	private String firstName;

	private String username;

	private String password;

	private String gender;

	private String education;

	private Date birthDate;

	private String note;

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
	public Long getTimeStamp() {
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

	@Column(name = FieldConstants.FIRST_NAME, nullable = false)
	public String getFirstName() {
		return firstName;
	}

	protected void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = FieldConstants.USERNAME, nullable = false)
	public String getUsername() {
		return username;
	}

	protected void setUsername(String username) {
		this.username = username;
	}

	@Column(name = FieldConstants.PASSWORD, nullable = false)
	public String getPassword() {
		return password;
	}

	protected void setPassword(String password) {
		this.password = password;
	}

	@Column(name = FieldConstants.GENDER, nullable = false)
	public String getGender() {
		return gender;
	}

	protected void setGender(String gender) {
		this.gender = gender;
	}

	@Column(name = FieldConstants.EDUCATION, nullable = false)
	public String getEducation() {
		return education;
	}

	protected void setEducation(String education) {
		this.education = education;
	}

	@Column(name = FieldConstants.BIRTH_DATE)
	public Date getBirthDate() {
		return birthDate;
	}

	protected void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	@Column(name = FieldConstants.NOTE, nullable = false)
	public String getNote() {
		return note;
	}

	protected void setNote(String note) {
		this.note = note;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExportScore == false)
			return false;

		final ExportScore other = (ExportScore) obj;
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
		if (!Objects.equals(getFirstName(), other.getFirstName()))
			return false;
		if (!Objects.equals(getUsername(), other.getUsername()))
			return false;
		if (!Objects.equals(getPassword(), other.getPassword()))
			return false;
		if (!Objects.equals(getGender(), other.getGender()))
			return false;
		if (!Objects.equals(getEducation(), other.getEducation()))
			return false;
		if (!Objects.equals(getBirthDate(), other.getBirthDate()))
			return false;
		if (!Objects.equals(getNote(), other.getNote()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExportScore [id=" + id + ", timeStamp=" + timeStamp + ", name=" + name + ", branchId=" + branchId
				+ ", branchName=" + branchName + ", taskId=" + taskId + ", taskName=" + taskName + ", slideId="
				+ slideId + ", slideName=" + slideName + ", testId=" + testId + ", userId=" + userId + ", packId="
				+ packId + ", packName=" + packName + ", created=" + created + ", firstName=" + firstName
				+ ", username=" + username + ", password=" + password + ", gender=" + gender + ", education="
				+ education + ", birthDate=" + birthDate + ", note=" + note + ", data=" + data + "]";
	}

}
