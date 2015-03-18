/**
 * 
 */
package org.hypothesis.entity;

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
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for user groups
 * 
 */
@Entity
@Table(name = "TBL_GROUP")
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "groupGenerator")
	@SequenceGenerator(name = "groupGenerator", sequenceName = "hbn_group_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	public Long getId() {
		return super.getId();
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
	}

	@Column(name = "NAME", nullable = false, unique = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "NOTE")
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Column(name = "OWNER_ID", nullable = false)
	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	@ManyToMany(targetEntity = org.hypothesis.entity.User.class, cascade = {
			CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "TBL_GROUP_USER", joinColumns = @JoinColumn(name = "GROUP_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
	@Cascade({ org.hibernate.annotations.CascadeType.MERGE })
	@LazyCollection(LazyCollectionOption.FALSE)
	public Set<User> getUsers() {
		return users;
	}

	protected void setUsers(Set<User> set) {
		this.users = set;
	}

	public void addUser(User user) {
		this.users.add(user);
	}

	public void removeUser(User user) {
		this.users.remove(user);
	}

	@Override
	public boolean equals(Object obj) {
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
	public int hashCode() {
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
	public String toString() {
		return getName();
	}

}
