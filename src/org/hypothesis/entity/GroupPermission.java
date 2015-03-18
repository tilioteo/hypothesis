/**
 * 
 */
package org.hypothesis.entity;

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

import org.hypothesis.common.SerializableIdObject;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for group permitions this entity holds information of
 *         which user group has permition to pack
 * 
 */
@Entity
@Table(name = "TBL_GROUP_PERMISSION", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"GROUP_ID", "PACK_ID" }) })
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "groupPermissionGenerator")
	@SequenceGenerator(name = "groupPermissionGenerator", sequenceName = "hbn_group_permission_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	public Long getId() {
		return super.getId();
	}

	@ManyToOne
	@JoinColumn(name = "GROUP_ID", nullable = false)
	public Group getGroup() {
		return group;
	}

	protected void setGroup(Group group) {
		this.group = group;
	}

	@ManyToOne
	@JoinColumn(name = "PACK_ID", nullable = false)
	public Pack getPack() {
		return pack;
	}

	protected void setPack(Pack pack) {
		this.pack = pack;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GroupPermission))
			return false;
		GroupPermission other = (GroupPermission) obj;
		/*
		 * if (getId() == null) { if (other.getId() != null) return false; }
		 * else if (!getId().equals(other.getId())) return false;
		 */
		// TODO remove when Buffered.SourceException occurs and use ID
		if (getGroup() == null) {
			if (other.getGroup() != null)
				return false;
		} else if (!getGroup().equals(other.getGroup()))
			return false;
		if (getPack() == null) {
			if (other.getPack() != null)
				return false;
		} else if (!getPack().equals(other.getPack()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 181;
		int result = 1;
		// result = prime * result + ((getId() == null) ? 0 :
		// getId().hashCode());
		// TODO remove when Buffered.SourceException occurs and use ID
		result = prime * result
				+ ((getGroup() == null) ? 0 : getGroup().hashCode());
		result = prime * result
				+ ((getPack() == null) ? 0 : getPack().hashCode());
		return result;
	}

}
