/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.oldmodel;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.TableConstants;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@Entity
@Table(name = TableConstants.ROLE_TABLE)
@Access(AccessType.PROPERTY)
@Deprecated
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.ROLE_GENERATOR)
	@SequenceGenerator(name = TableConstants.ROLE_GENERATOR, sequenceName = TableConstants.ROLE_SEQUENCE, initialValue = 1, allocationSize = 1)
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

	@Override
	public boolean equals(Object obj) {
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

		final int prime = 23;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return getName();
	}

}
