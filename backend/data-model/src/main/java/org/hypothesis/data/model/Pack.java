package org.hypothesis.data.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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
import javax.persistence.OrderColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.HasId;
import org.hypothesis.data.interfaces.TableConstants;

@SuppressWarnings("serial")
@Entity
@Table(name = TableConstants.PACK_TABLE)
@Access(AccessType.PROPERTY)
public class Pack implements Serializable, HasId<Long> {

	private Long id;

	private String name;

	private String description;

	/**
	 * pack is published to users
	 */
	private Boolean published = false;

	/**
	 * pack requires java to run
	 */
	// not used
	// private Boolean javaRequired = true;

	private String note;

	// not used
	// private Pack enableAfter;

	/**
	 * list of contained branches
	 */
	private List<Branch> branches;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.PACK_GENERATOR)
	@SequenceGenerator(name = TableConstants.PACK_GENERATOR, sequenceName = TableConstants.PACK_SEQUENCE, initialValue = 1, allocationSize = 1)
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

	@Column(name = FieldConstants.DESCRIPTION)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = FieldConstants.PUBLISHED)
	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	@Column(name = FieldConstants.NOTE)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = TableConstants.PACK_BRANCH_TABLE, joinColumns = @JoinColumn(name = FieldConstants.PACK_ID), inverseJoinColumns = @JoinColumn(name = FieldConstants.BRANCH_ID))
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.TRUE)
	@OrderColumn(name = FieldConstants.RANK)
	public List<Branch> getBranches() {
		return branches;
	}

	public void setBranches(List<Branch> list) {
		this.branches = list;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pack == false)
			return false;

		final Pack other = (Pack) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(getName(), other.getName()))
			return false;
		if (!Objects.equals(getDescription(), other.getDescription()))
			return false;
		if (!Objects.equals(getPublished(), other.getPublished()))
			return false;
		if (!Objects.equals(getNote(), other.getNote()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Pack [id=" + id + ", name=" + name + ", description=" + description + ", published=" + published
				+ ", note=" + note + "]";
	}

}
