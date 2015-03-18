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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hypothesis.common.SerializableIdObject;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for role
 * 
 */
@Entity
@Table(name = "TBL_ROLE")
@Access(AccessType.PROPERTY)
public final class Role extends SerializableIdObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -713813038487970375L;

	private String name;

	protected Role() {
		super();
	}

	public Role(String name) {
		this();
		this.name = name;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roleGenerator")
	@SequenceGenerator(name = "roleGenerator", sequenceName = "hbn_role_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	public Long getId() {
		return super.getId();
	}

	@Column(name = "NAME", nullable = false, unique = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Role))
			return false;
		Role other = (Role) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		// TODO remove when Buffered.SourceException occurs
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 139;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		// TODO remove when Buffered.SourceException occurs
		result = prime * result
				+ ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	@Override
	public String toString() {
		return getName();
	}

}
