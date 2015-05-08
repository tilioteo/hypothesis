/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for group permitions this entity holds information of
 *         which user group has permition to pack
 * 
 */
@Entity
@Table(name = TableConstants.GROUP_PERMISSION_TABLE, uniqueConstraints = { @UniqueConstraint(columnNames = {
		FieldConstants.GROUP_ID, FieldConstants.PACK_ID }) })
@Access(AccessType.PROPERTY)
public final class GroupPermission extends SerializableIdObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8331070869109730350L;

	private Group group;
	private Pack pack;

	protected GroupPermission() {
		super();
	}

	public GroupPermission(Group group, Pack pack) {
		this();
		this.group = group;
		this.pack = pack;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.GROUP_PERMISSION_GENERATOR)
	@SequenceGenerator(name = TableConstants.GROUP_PERMISSION_GENERATOR, sequenceName = TableConstants.GROUP_PERMISSION_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return super.getId();
	}

	@ManyToOne
	@JoinColumn(name = FieldConstants.GROUP_ID, nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	public Group getGroup() {
		return group;
	}

	protected void setGroup(Group group) {
		this.group = group;
	}

	@ManyToOne
	@JoinColumn(name = FieldConstants.PACK_ID, nullable = false)
	public Pack getPack() {
		return pack;
	}

	protected void setPack(Pack pack) {
		this.pack = pack;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GroupPermission)) {
			return false;
		}
		GroupPermission other = (GroupPermission) obj;
		
		Long id = getId();
		Long id2 = other.getId();
		Group group = getGroup();
		Group group2 = other.getGroup();
		Pack pack = getPack();
		Pack pack2 = other.getPack();

		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (group == null) {
			if (group2 != null) {
				return false;
			}
		} else if (!group.equals(group2)) {
			return false;
		}
		
		if (pack == null) {
			if (pack2 != null) {
				return false;
			}
		} else if (!pack.equals(pack2)) {
			return false;
		}
		
		return true;
	}

	@Override
	public int hashCode() {
		Long id = getId();
		Group group = getGroup();
		Pack pack = getPack();

		final int prime = 17;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result	+ (group != null ? group.hashCode() : 0);
		result = prime * result	+ (pack != null ? pack.hashCode() : 0);
		return result;
	}

}
