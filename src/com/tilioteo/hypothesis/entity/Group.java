/**
 * 
 */
package com.tilioteo.hypothesis.entity;

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
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for user groups
 * 
 */
@Entity
@Table(name = EntityTableConstants.GROUP_TABLE)
@Access(AccessType.PROPERTY)
public final class Group extends SerializableIdObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2666394472480823648L;

	private String name;
	private String note;
	private Long ownerId;

	/**
	 * set of users which belong to group
	 */
	private Set<User> users = new HashSet<User>();

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EntityTableConstants.GROUP_GENERATOR)
	@SequenceGenerator(name = EntityTableConstants.GROUP_GENERATOR, sequenceName = EntityTableConstants.GROUP_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = EntityFieldConstants.ID)
	public final Long getId() {
		return super.getId();
	}

	@Override
	public final void setId(Long id) {
		super.setId(id);
	}

	@Column(name = EntityFieldConstants.NAME, nullable = false, unique = true)
	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	@Column(name = EntityFieldConstants.NOTE, nullable = true)
	public final String getNote() {
		return note;
	}

	public final void setNote(String note) {
		this.note = note;
	}

	@Column(name = EntityFieldConstants.OWNER_ID, nullable = false)
	public final Long getOwnerId() {
		return ownerId;
	}

	public final void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	@ManyToMany(targetEntity = User.class, cascade = {
			CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = EntityTableConstants.GROUP_USER_TABLE, joinColumns = @JoinColumn(name = EntityFieldConstants.GROUP_ID), inverseJoinColumns = @JoinColumn(name = EntityFieldConstants.USER_ID))
	@Cascade({ org.hibernate.annotations.CascadeType.MERGE })
	@LazyCollection(LazyCollectionOption.FALSE)
	public final Set<User> getUsers() {
		return users;
	}

	protected void setUsers(Set<User> set) {
		this.users = set;
	}

	public final void addUser(User user) {
		this.users.add(user);
	}

	public final void removeUser(User user) {
		this.users.remove(user);
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Group))
			return false;
		Group other = (Group) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		if (getNote() == null) {
			if (other.getNote() != null)
				return false;
		} else if (!getNote().equals(other.getNote()))
			return false;
		/*
		 * if (getUsers() == null) { if (other.getUsers() != null) return false;
		 * } else if (!getUsers().equals(other.getUsers())) return false;
		 */
		return true;
	}

	@Override
	public final int hashCode() {
		final int prime = 163;
		int result = 1;
		result = prime * result + (getId() == null ? 0 : getId().hashCode());
		result = prime * result
				+ ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result
				+ ((getNote() == null) ? 0 : getNote().hashCode());
		// result = prime * result + ((getUsers() == null) ? 0 :
		// getUsers().hashCode());*/
		return result;
	}

	@Override
	public final String toString() {
		return getName();
	}

}
