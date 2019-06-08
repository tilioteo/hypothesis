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
@Table(name = TableConstants.PACK_SET_TABLE)
@Access(AccessType.PROPERTY)
public class PackSet implements Serializable, HasId<Long> {

	private Long id;

	private String name;

	/**
	 * list of contained packs
	 */
	private List<Pack> packs;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.PACK_SET_GENERATOR)
	@SequenceGenerator(name = TableConstants.PACK_SET_GENERATOR, sequenceName = TableConstants.PACK_SET_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public void setPacks(List<Pack> list) {
		this.packs = list;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PackSet == false)
			return false;

		final PackSet other = (PackSet) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(getName(), other.getName()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PackSet [id=" + id + ", name=" + name + "]";
	}

}
