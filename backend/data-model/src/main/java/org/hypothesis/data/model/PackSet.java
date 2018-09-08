package org.hypothesis.data.model;

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
import org.hypothesis.data.interfaces.HasList;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@Entity
@Table(name = TableConstants.PACK_SET_TABLE)
@Access(AccessType.PROPERTY)
public class PackSet extends SerializableIdObject implements HasList<Pack> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4836797614782216511L;

	private String name = "";

	/**
	 * list of contained packs
	 */
	private List<Pack> packs = new LinkedList<>();

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.PACK_SET_GENERATOR)
	@SequenceGenerator(name = TableConstants.PACK_SET_GENERATOR, sequenceName = TableConstants.PACK_SET_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return super.getId();
	}

	@Column(name = FieldConstants.NAME, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = TableConstants.PACK_SET_PACK_TABLE, joinColumns = @JoinColumn(name = FieldConstants.PACK_SET_ID), inverseJoinColumns = @JoinColumn(name = FieldConstants.PACK_ID))
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.TRUE)
	@OrderColumn(name = FieldConstants.RANK)
	public List<Pack> getPacks() {
		return packs;
	}

	protected void setPacks(List<Pack> list) {
		this.packs = list;
	}

	@Transient
	public final List<Pack> getList() {
		return getPacks();
	}

	public final void addPack(Pack p) {
		getPacks().add(p);
	}

	public final void removePack(Pack p) {
		getPacks().remove(p);
	}

	public final void removeAllPacks() {
		getPacks().clear();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		PackSet other = (PackSet) obj;

		Long id = getId();
		Long id2 = other.getId();
		String name = getName();
		String name2 = other.getName();

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

		return true;
	}

	@Override
	public int hashCode() {
		Long id = getId();
		String name = getName();
		List<Pack> packs = getPacks();

		final int prime = 31;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result + (name != null ? name.hashCode() : 0);
		result = prime * result + packs.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return getName();
	}
}
