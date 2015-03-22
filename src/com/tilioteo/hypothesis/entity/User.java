/**
 * 
 */
package com.tilioteo.hypothesis.entity;

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

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.common.EntityTableConstants;

/**
 * 
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for users
 * 
 */
@Entity
@Table(name = EntityTableConstants.USER_TABLE)
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EntityTableConstants.USER_GENERATOR)
	@SequenceGenerator(name = EntityTableConstants.USER_GENERATOR, sequenceName = EntityTableConstants.USER_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = EntityFieldConstants.ID)
	public Long getId() {
		return super.getId();
	}

	@Column(name = EntityFieldConstants.USERNAME, nullable = false, unique = true)
	public String getUsername() {
		return username;
	}

	/**
	 * user name must be unique and not null
	 * 
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = EntityFieldConstants.PASSWORD, nullable = false)
	public String getPassword() {
		return password;
	}

	/**
	 * password cannot be null
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = EntityFieldConstants.ENABLED, nullable = false)
	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = EntityFieldConstants.EXPIRE_DATE)
	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	@Column(name = EntityFieldConstants.NOTE, nullable = true)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = EntityTableConstants.USER_ROLE_TABLE, joinColumns = @JoinColumn(name = EntityFieldConstants.USER_ID), inverseJoinColumns = @JoinColumn(name = EntityFieldConstants.ROLE_ID))
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.FALSE)
	public Set<Role> getRoles() {
		return roles;
	}

	protected void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@ManyToMany(targetEntity = Group.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = EntityTableConstants.GROUP_USER_TABLE, joinColumns = @JoinColumn(name = EntityFieldConstants.USER_ID), inverseJoinColumns = @JoinColumn(name = EntityFieldConstants.GROUP_ID))
	@Cascade({ org.hibernate.annotations.CascadeType.MERGE })
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<Group> getGroups() {
		return groups;
	}

	protected void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	@Column(name = EntityFieldConstants.OWNER_ID, nullable = true)
	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public final void addRole(Role role) {
		getRoles().add(role);
	}

	public final void removeRole(Role role) {
		getRoles().remove(role);
	}

	public final void addGroup(Group group) {
		getGroups().add(group);
	}

	public final void removeGroup(Group group) {
		getGroups().remove(group);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		
		Long id = getId();
		Long id2 = other.getId();
		String username = getUsername();
		String username2 = other.getUsername();
		String password = getPassword();
		String password2 = other.getPassword();
		Boolean enabled = getEnabled();
		Boolean enabled2 = other.getEnabled();
		Date expireDate = getExpireDate();
		Date expireDate2 = other.getExpireDate();
		String note = getNote();
		String note2 = other.getNote();
		Long ownerId = getOwnerId();
		Long ownerId2 = other.getOwnerId();
		
		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (username != null && !username.equals(username2)) {
			return false;
		} else if (username2 != null) {
			return false;
		}
		
		if (password != null && !password.equals(password2)) {
			return false;
		} else if (password2 != null) {
			return false;
		}
		
		if (enabled != null && !enabled.equals(enabled2)) {
			return false;
		} else if (enabled2 != null) {
			return false;
		}
		
		if (expireDate != null && !expireDate.equals(expireDate2)) {
			return false;
		} else if (expireDate2 != null) {
			return false;
		}
		
		if (note != null && !note.equals(note2)) {
			return false;
		} else if (note2 != null) {
			return false;
		}
		
		if (ownerId != null && !ownerId.equals(ownerId2)) {
			return false;
		} else if (ownerId2 != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		Long id = getId();
		String username = getUsername();
		String password = getPassword();
		Boolean enabled = getEnabled();
		Date expireDate = getExpireDate();
		String note = getNote();
		Long ownerId = getOwnerId();
		Set<Role> roles = getRoles();
		Set<Group> groups = getGroups();

		final int prime = 61;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result	+ (username != null ? username.hashCode() : 0);
		result = prime * result	+ (password != null ? password.hashCode() : 0);
		result = prime * result	+ (enabled != null ? enabled.hashCode() : 0);
		result = prime * result + (expireDate != null ? expireDate.hashCode() : 0);
		result = prime * result + (note != null ? note.hashCode() : 0);
		result = prime * result + (ownerId != null ? ownerId.hashCode() : 0);
		result = prime * result	+ roles.hashCode();
		result = prime * result + groups.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return getUsername();
	}

}
