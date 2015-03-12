/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import java.util.LinkedList;
import java.util.List;

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
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.common.EntityTableConstants;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for testing pack
 * 
 */
@Entity
@Table(name = EntityTableConstants.PACK_TABLE)
@Access(AccessType.PROPERTY)
public final class Pack extends SerializableIdObject implements HasList<Branch> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3225002856411893041L;

	private String name = "";
	private String description = "";

	/**
	 * pack is published to users
	 */
	private Boolean published = false;
	private String note;

	/**
	 * list of contained branches
	 */
	private List<Branch> branches = new LinkedList<Branch>();

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EntityTableConstants.PACK_GENERATOR)
	@SequenceGenerator(name = EntityTableConstants.PACK_GENERATOR, sequenceName = EntityTableConstants.PACK_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = EntityFieldConstants.ID)
	public final Long getId() {
		return super.getId();
	}

	@Column(name = EntityFieldConstants.NAME, nullable = false, unique = true)
	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	@Column(name = EntityFieldConstants.DESCRIPTION)
	public final String getDescription() {
		return description;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	@Column(name = EntityFieldConstants.PUBLISHED)
	public final Boolean getPublished() {
		return published;
	}

	public final void setPublished(Boolean published) {
		this.published = published;
	}

	@Column(name = EntityFieldConstants.NOTE)
	public final String getNote() {
		return note;
	}

	public final void setNote(String note) {
		this.note = note;
	}

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = EntityTableConstants.PACK_BRANCH_TABLE, joinColumns = @JoinColumn(name = EntityFieldConstants.PACK_ID), inverseJoinColumns = @JoinColumn(name = EntityFieldConstants.BRANCH_ID))
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.TRUE)
	@OrderColumn(name = EntityFieldConstants.RANK)
	public List<Branch> getBranches() {
		return branches;
	}

	protected void setBranches(List<Branch> list) {
		this.branches = list;
	}

	@Transient
	public final List<Branch> getList() {
		return getBranches();
	}

	public final void addBranch(Branch b) {
		getBranches().add(b);
	}

	public final void removeBranch(Branch b) {
		getBranches().remove(b);
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Pack)) {
			return false;
		}
		Pack other = (Pack) obj;
		
		Long id = getId();
		Long id2 = other.getId();
		String name = getName();
		String name2 = other.getName();
		String description = getDescription();
		String description2 = other.getDescription();
		Boolean published = getPublished();
		Boolean published2 = other.getPublished();
		String note = getNote();
		String note2 = other.getNote();
		//List<Branch> branches = getBranches();
		//List<Branch> branches2 = other.getBranches();

		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (name != null && !name.equals(name2)) {
			return false;
		} else if (name2 != null) {
			return false;
		}
		
		if (description != null && !description.equals(description2)) {
			return false;
		} else if (description2 != null) {
			return false;
		}
		
		if (published != null && !published.equals(published2)) {
			return false;
		} else if (published2 != null) {
			return false;
		}
		
		if (note != null && !note.equals(note2)) {
			return false;
		} else if (note2 != null) {
			return false;
		}

		return true;
	}

	@Override
	public final int hashCode() {
		Long id = getId();
		String name = getName();
		String description = getDescription();
		Boolean published = getPublished();
		String note = getNote();
		List<Branch> branches = getBranches();
		
		final int prime = 19;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result + (name != null ? name.hashCode() : 0);
		result = prime * result	+ (description != null ? description.hashCode() : 0);
		result = prime * result	+ (published != null ? published.hashCode() : 0);
		result = prime * result	+ (note != null ? note.hashCode() : 0);
		result = prime * result	+ branches.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return getName();
	}

}
