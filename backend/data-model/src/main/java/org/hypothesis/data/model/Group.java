package org.hypothesis.data.model;

import java.io.Serializable;
import java.util.Objects;
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

import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.HasId;
import org.hypothesis.data.interfaces.TableConstants;

@SuppressWarnings("serial")
@Entity
@Table(name = TableConstants.GROUP_TABLE)
@Access(AccessType.PROPERTY)
public class Group implements Serializable, HasId<Long> {

	private Long id;

	private String name;

	private String note;

	private Long ownerId;

	/**
	 * set of users which belong to group
	 */
	private Set<User> users;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.GROUP_GENERATOR)
	@SequenceGenerator(name = TableConstants.GROUP_GENERATOR, sequenceName = TableConstants.GROUP_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	@Column(name = FieldConstants.OWNER_ID)
	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	/*
	 * @ManyToMany(targetEntity = User.class, fetch = FetchType.LAZY,
	 * mappedBy="groups")
	 * 
	 * @JoinTable(name = TableConstants.GROUP_USER_TABLE, joinColumns
	 * = @JoinColumn(name = FieldConstants.GROUP_ID), inverseJoinColumns
	 * = @JoinColumn(name = FieldConstants.USER_ID))
	 * 
	 * @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	 * 
	 * @LazyCollection(LazyCollectionOption.TRUE)
	 */
	@ManyToMany
	@JoinTable(name = TableConstants.GROUP_USER_TABLE, joinColumns = {
			@JoinColumn(name = FieldConstants.GROUP_ID) }, inverseJoinColumns = {
					@JoinColumn(name = FieldConstants.USER_ID) })
	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> set) {
		this.users = set;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Group == false)
			return false;

		final Group other = (Group) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(getName(), other.getName()))
			return false;
		if (!Objects.equals(getNote(), other.getNote()))
			return false;
		if (!Objects.equals(getOwnerId(), other.getOwnerId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Group [id=" + id + ", name=" + name + ", note=" + note + ", ownerId=" + ownerId + "]";
	}

}
