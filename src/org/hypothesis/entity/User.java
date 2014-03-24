/**
 * 
 */
package org.hypothesis.entity;

import java.util.Date;
import java.util.HashSet;
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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hypothesis.common.SerializableIdObject;

/**
 * 
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for users
 * 
 */
@Entity
@Table(name = "TBL_USER")
@Access(AccessType.PROPERTY)
public final class User extends SerializableIdObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8876650436162678841L;

	private String username;
	private String password;

	/**
	 * user account is enabled
	 */
	private Boolean enabled = true;

	/**
	 * user account will expire at
	 */
	private Date expireDate;

	private String note;

	/**
	 * user has roles
	 */
	private Set<Role> roles = new HashSet<Role>();

	/**
	 * user belongs to groups
	 */
	private Set<Group> groups = new HashSet<Group>();

	/**
	 * user can have another user (id of user) as owner
	 */
	private Long ownerId;

	/**
	 * database id is generated
	 */
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userGenerator")
	@SequenceGenerator(name = "userGenerator", sequenceName = "hbn_user_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	public final Long getId() {
		return super.getId();
	}

	@Column(name = "USERNAME", nullable = false, unique = true)
	public final String getUsername() {
		return username;
	}

	/**
	 * user name must be unique and not null
	 * 
	 * @param username
	 */
	public final void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "PASSWORD", nullable = false)
	public final String getPassword() {
		return password;
	}

	/**
	 * password cannot be null
	 * 
	 * @param password
	 */
	public final void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "ENABLED", nullable = false)
	public final Boolean getEnabled() {
		return enabled;
	}

	public final void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = "EXPIRE_DATE")
	public final Date getExpireDate() {
		return expireDate;
	}

	public final void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	@Column(name = "NOTE", nullable = true)
	public final String getNote() {
		return note;
	}

	public final void setNote(String note) {
		this.note = note;
	}

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "TBL_USER_ROLE", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.FALSE)
	public final Set<Role> getRoles() {
		return roles;
	}

	protected void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@ManyToMany(targetEntity = org.hypothesis.entity.Group.class, cascade = {
			CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "TBL_GROUP_USER", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "GROUP_ID"))
	@Cascade({ org.hibernate.annotations.CascadeType.MERGE })
	@LazyCollection(LazyCollectionOption.FALSE)
	public final Set<Group> getGroups() {
		return groups;
	}

	protected void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	@Column(name = "OWNER_ID", nullable = true)
	public final Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public final void addRole(Role role) {
		this.roles.add(role);
	}

	public final void removeRole(Role role) {
		this.roles.remove(role);
	}

	public final void addGroup(Group group) {
		this.groups.add(group);
	}

	public final void removeGroup(Group group) {
		this.groups.remove(group);
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		if (getEnabled() == null) {
			if (other.getEnabled() != null)
				return false;
		} else if (!getEnabled().equals(other.getEnabled()))
			return false;
		if (getExpireDate() == null) {
			if (other.getExpireDate() != null)
				return false;
		} else if (!getExpireDate().equals(other.getExpireDate()))
			return false;
		/*
		 * if (getGroups() == null) { if (other.getGroups() != null) return
		 * false; } else if (!getGroups().equals(other.getGroups())) return
		 * false;
		 */
		if (getNote() == null) {
			if (other.getNote() != null)
				return false;
		} else if (!getNote().equals(other.getNote()))
			return false;
		if (getPassword() == null) {
			if (other.getPassword() != null)
				return false;
		} else if (!getPassword().equals(other.getPassword()))
			return false;
		if (getRoles() == null) {
			if (other.getRoles() != null)
				return false;
		} else if (!getRoles().equals(other.getRoles()))
			return false;
		if (getUsername() == null) {
			if (other.getUsername() != null)
				return false;
		} else if (!getUsername().equals(other.getUsername()))
			return false;
		return true;
	}

	@Override
	public final int hashCode() {
		final int prime = 113;
		int result = 1;
		result = prime * result + (getId() == null ? 0 : getId().hashCode());
		result = prime * result
				+ ((getEnabled() == null) ? 0 : getEnabled().hashCode());
		result = prime * result
				+ ((getExpireDate() == null) ? 0 : getExpireDate().hashCode());
		// result = prime * result + ((getGroups() == null) ? 0 :
		// getGroups().hashCode());
		result = prime * result
				+ ((getNote() == null) ? 0 : getNote().hashCode());
		result = prime * result
				+ ((getPassword() == null) ? 0 : getPassword().hashCode());
		result = prime * result
				+ ((getRoles() == null) ? 0 : getRoles().hashCode());
		result = prime * result
				+ ((getUsername() == null) ? 0 : getUsername().hashCode());
		return result;
	}

	@Override
	public final String toString() {
		return getUsername();
	}

}
