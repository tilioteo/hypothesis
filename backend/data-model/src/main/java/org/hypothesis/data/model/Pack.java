/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hypothesis.data.interfaces.HasList;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@Entity
@Table(name = TableConstants.PACK_TABLE)
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

	/**
	 * pack requires java to run
	 */
	private Boolean javaRequired = true;

	private String note;

	/**
	 * list of contained branches
	 */
	private List<Branch> branches = new LinkedList<>();

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.PACK_GENERATOR)
	@SequenceGenerator(name = TableConstants.PACK_GENERATOR, sequenceName = TableConstants.PACK_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return super.getId();
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
	public Boolean isPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	@Column(name = FieldConstants.JAVA_REQUIRED)
	public Boolean isJavaRequired() {
		return javaRequired;
	}

	public void setJavaRequired(Boolean javaRequired) {
		this.javaRequired = javaRequired;
	}

	@Column(name = FieldConstants.NOTE)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = TableConstants.PACK_BRANCH_TABLE, joinColumns = @JoinColumn(name = FieldConstants.PACK_ID) , inverseJoinColumns = @JoinColumn(name = FieldConstants.BRANCH_ID) )
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.TRUE)
	@OrderColumn(name = FieldConstants.RANK)
	public List<Branch> getBranches() {
		return branches;
	}

	protected void setBranches(List<Branch> list) {
		this.branches = list;
	}

	@Transient
	@Override
	public List<Branch> getList() {
		return getBranches();
	}

	public void addBranch(Branch b) {
		getBranches().add(b);
	}

	public void removeBranch(Branch b) {
		getBranches().remove(b);
	}

	@Override
	public boolean equals(Object obj) {
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
		Boolean published = isPublished();
		Boolean published2 = other.isPublished();
		String note = getNote();
		String note2 = other.getNote();
		// List<Branch> branches = getBranches();
		// List<Branch> branches2 = other.getBranches();

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

		if (description == null) {
			if (description2 != null) {
				return false;
			}
		} else if (!description.equals(description2)) {
			return false;
		}

		if (published == null) {
			if (published2 != null) {
				return false;
			}
		} else if (!published.equals(published2)) {
			return false;
		}

		if (note == null) {
			if (note2 != null) {
				return false;
			}
		} else if (!note.equals(note2)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		Long id = getId();
		String name = getName();
		String description = getDescription();
		Boolean published = isPublished();
		String note = getNote();
		List<Branch> branches = getBranches();

		final int prime = 19;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result + (name != null ? name.hashCode() : 0);
		result = prime * result + (description != null ? description.hashCode() : 0);
		result = prime * result + (published != null ? published.hashCode() : 0);
		result = prime * result + (note != null ? note.hashCode() : 0);
		result = prime * result + branches.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return getName();
	}

}
