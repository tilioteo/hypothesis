package org.hypothesis.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.HasId;
import org.hypothesis.data.interfaces.TableConstants;

@SuppressWarnings("serial")
@Entity
@Table(name = TableConstants.USER_TABLE, uniqueConstraints = {
		// VN specific - login by surname as user name and identity number as
		// password
		@UniqueConstraint(columnNames = { FieldConstants.USERNAME, FieldConstants.PASSWORD }) })
@Access(AccessType.PROPERTY)
public class User implements Serializable, HasId<Long> {

	private Long id;

	private String username;

	private String password;

	/**
	 * user account is enabled
	 */
	private boolean enabled;

	/**
	 * disable current processed pack
	 */
	private boolean autoDisable;

	/**
	 * user is temporarily suspended for testing
	 */
	private boolean testingSuspended;

	/**
	 * user account will expire at
	 */
	private Date expireDate;

	private String note;

	/**
	 * user can have another user (id of user) as owner
	 */
	private Long ownerId;

	private String name;

	private String gender;

	private String education;

	private Date birthDate;

	private Date testingDate;

	/**
	 * user has roles
	 */
	private Set<Role> roles;

	/**
	 * user belongs to groups
	 */
	private Set<Group> groups;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.USER_GENERATOR)
	@SequenceGenerator(name = TableConstants.USER_GENERATOR, sequenceName = TableConstants.USER_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// VN specific - login by surname as user name and identity number as
	// password
	@Column(name = FieldConstants.USERNAME, nullable = false/*
															 * , unique = true
															 */)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * password cannot be null
	 * 
	 */
	@Column(name = FieldConstants.PASSWORD, nullable = false)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = FieldConstants.ENABLED, nullable = false)
	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = FieldConstants.AUTO_DISABLE, nullable = false)
	public boolean getAutoDisable() {
		return autoDisable;
	}

	public void setAutoDisable(boolean autoDisable) {
		this.autoDisable = autoDisable;
	}

	@Column(name = FieldConstants.TESTING_SUSPENDED, nullable = false)
	public boolean isTestingSuspended() {
		return testingSuspended;
	}

	public void setTestingSuspended(boolean testingSuspended) {
		this.testingSuspended = testingSuspended;
	}

	@Column(name = FieldConstants.EXPIRE_DATE)
	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	@Column(name = FieldConstants.NOTE, nullable = true)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Column(name = FieldConstants.OWNER_ID, nullable = true)
	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	@Column(name = FieldConstants.NAME, nullable = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = FieldConstants.GENDER, nullable = true, length = 1)
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Column(name = FieldConstants.EDUCATION, nullable = true)
	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	@Column(name = FieldConstants.BIRTH_DATE)
	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	@Column(name = FieldConstants.TESTING_DATE)
	public Date getTestingDate() {
		return testingDate;
	}

	public void setTestingDate(Date testingDate) {
		this.testingDate = testingDate;
	}

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = TableConstants.USER_ROLE_TABLE, joinColumns = @JoinColumn(name = FieldConstants.USER_ID), inverseJoinColumns = @JoinColumn(name = FieldConstants.ROLE_ID))
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.FALSE)
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	/*
	 * @ManyToMany(fetch = FetchType.LAZY)
	 * 
	 * @JoinTable(name = TableConstants.GROUP_USER_TABLE, joinColumns
	 * = @JoinColumn(name = FieldConstants.USER_ID), inverseJoinColumns
	 * = @JoinColumn(name = FieldConstants.GROUP_ID))
	 * 
	 * @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	 * 
	 * @LazyCollection(LazyCollectionOption.TRUE)
	 */
	@ManyToMany(mappedBy = "users")
	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User == false)
			return false;

		final User other = (User) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(getUsername(), other.getUsername()))
			return false;
		if (!Objects.equals(getPassword(), other.getPassword()))
			return false;
		if (!Objects.equals(getEnabled(), other.getEnabled()))
			return false;
		if (!Objects.equals(getAutoDisable(), other.getAutoDisable()))
			return false;
		if (!Objects.equals(isTestingSuspended(), other.isTestingSuspended()))
			return false;
		if (!Objects.equals(getExpireDate(), other.getExpireDate()))
			return false;
		if (!Objects.equals(getNote(), other.getNote()))
			return false;
		if (!Objects.equals(getOwnerId(), other.getOwnerId()))
			return false;
		if (!Objects.equals(getName(), other.getName()))
			return false;
		if (!Objects.equals(getGender(), other.getGender()))
			return false;
		if (!Objects.equals(getEducation(), other.getEducation()))
			return false;
		if (!Objects.equals(getBirthDate(), other.getBirthDate()))
			return false;
		if (!Objects.equals(getTestingDate(), other.getTestingDate()))
			return false;
		if (!Objects.equals(getUsername(), other.getUsername()))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", enabled=" + enabled + ", autoDisable=" + autoDisable
				+ ", testingSuspended=" + testingSuspended + ", expireDate=" + expireDate + ", ownerId=" + ownerId
				+ ", testingDate=" + testingDate + ", roles=" + roles + "]";
	}

}
