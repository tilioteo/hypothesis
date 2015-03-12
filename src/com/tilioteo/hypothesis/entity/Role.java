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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.common.EntityTableConstants;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for role
 * 
 */
@Entity
@Table(name = EntityTableConstants.ROLE_TABLE)
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EntityTableConstants.ROLE_GENERATOR)
	@SequenceGenerator(name = EntityTableConstants.ROLE_GENERATOR, sequenceName = EntityTableConstants.ROLE_SEQUENCE, initialValue = 1, allocationSize = 1)
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

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Role)) {
			return false;
		}
		Role other = (Role) obj;
		
		Long id = getId();
		Long id2 = other.getId();
		String name = getName();
		String name2 = other.getName();
		
		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (name != null && !name.equals(name2)) {
			return false;
		} else if (name2 != null) {
			return false;
		}
		

		return true;
	}

	@Override
	public final int hashCode() {
		Long id = getId();
		String name = getName();
		
		final int prime = 23;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result	+ (name != null ? name.hashCode() : 0);
		return result;
	}

	@Override
	public final String toString() {
		return getName();
	}

}
