/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
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

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@Entity
@Table(name = TableConstants.GROUP_TABLE)
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.GROUP_GENERATOR)
	@SequenceGenerator(name = TableConstants.GROUP_GENERATOR, sequenceName = TableConstants.GROUP_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return super.getId();
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
	}

	@Column(name = FieldConstants.NAME, nullable = false, unique = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = FieldConstants.NOTE)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Column(name = FieldConstants.OWNER_ID, nullable = false)
	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	@ManyToMany(targetEntity = User.class/*
											 * , cascade = {CascadeType.PERSIST,
											 * CascadeType.MERGE }
											 */)
	@JoinTable(name = TableConstants.GROUP_USER_TABLE, joinColumns = @JoinColumn(name = FieldConstants.GROUP_ID) , inverseJoinColumns = @JoinColumn(name = FieldConstants.USER_ID) )
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<User> getUsers() {
		return users;
	}

	protected void setUsers(Set<User> set) {
		this.users = set;
	}

	public final void addUser(User user) {
		getUsers().add(user);
	}

	public final void removeUser(User user) {
		getUsers().remove(user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Group)) {
			return false;
		}
		Group other = (Group) obj;

		Long id = getId();
		Long id2 = other.getId();
		String name = getName();
		String name2 = other.getName();
		String note = getNote();
		String note2 = other.getNote();
		Long ownerId = getOwnerId();
		Long ownerId2 = other.getOwnerId();
		// Set<User> users = getUsers();
		// Set<User> users2 = other.getUsers();

		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (name == null) {
			if (name2 != null) {
				return false;
			}
		} else if (!name.equals(name2)) {
			return false;
		}

		if (note == null) {
			if (note2 != null) {
				return false;
			}
		} else if (!note.equals(note2)) {
			return false;
		}

		if (ownerId == null) {
			if (ownerId2 != null) {
				return false;
			}
		} else if (!ownerId.equals(ownerId2)) {
			return false;
		}

		/*
		 * if (getUsers() == null) { if (other.getUsers() != null) return false;
		 * } else if (!getUsers().equals(other.getUsers())) return false;
		 */
		return true;
	}

	@Override
	public int hashCode() {
		Long id = getId();
		String name = getName();
		String note = getNote();
		Long ownerId = getOwnerId();
		// Set<User> users = getUsers();

		final int prime = 13;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result + (name != null ? name.hashCode() : 0);
		result = prime * result + (note != null ? note.hashCode() : 0);
		result = prime * result + (ownerId != null ? ownerId.hashCode() : 0);
		// result = prime * result + users.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return getName();
	}

}
