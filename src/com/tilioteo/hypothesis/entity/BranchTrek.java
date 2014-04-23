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

import org.hibernate.annotations.Index;

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.common.EntityTableConstants;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for branch trek Branch trek is join object for
 *         relation between pack and branch identified by string key
 * 
 */
@Entity
@Table(name = EntityTableConstants.BRANCH_TREK_TABLE, uniqueConstraints = { @UniqueConstraint(columnNames = {
		EntityFieldConstants.PACK_ID, EntityFieldConstants.KEY, EntityFieldConstants.BRANCH_ID }) })
@org.hibernate.annotations.Table(appliesTo = EntityTableConstants.BRANCH_TREK_TABLE,
indexes = { @Index(name = "IX_PACK_BRANCH", columnNames = { EntityFieldConstants.PACK_ID, EntityFieldConstants.BRANCH_ID }),
		@Index(name="IX_KEY", columnNames = {EntityFieldConstants.KEY}) })
@Access(AccessType.PROPERTY)
public final class BranchTrek extends SerializableIdObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8262351809120504076L;

	private Pack pack;
	private String key;
	private Branch branch;

	protected BranchTrek() {
		super();
	}

	public BranchTrek(Pack pack, String key, Branch branch) {
		this();
		this.pack = pack;
		this.key = key;
		this.branch = branch;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EntityTableConstants.BRANCH_TREK_GENERATOR)
	@SequenceGenerator(name = EntityTableConstants.BRANCH_TREK_GENERATOR, sequenceName = EntityTableConstants.BRANCH_TREK_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = EntityFieldConstants.ID)
	public final Long getId() {
		return super.getId();
	}

	@ManyToOne
	@JoinColumn(name = EntityFieldConstants.PACK_ID, nullable = false)
	public final Pack getPack() {
		return pack;
	}

	protected void setPack(Pack pack) {
		this.pack = pack;
	}

	@Column(name = EntityFieldConstants.KEY, nullable = false)
	public final String getKey() {
		return key;
	}

	protected void setKey(String key) {
		this.key = key;
	}

	@ManyToOne
	@JoinColumn(name = EntityFieldConstants.BRANCH_ID, nullable = false)
	public final Branch getBranch() {
		return branch;
	}

	protected void setBranch(Branch branch) {
		this.branch = branch;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BranchTrek)) {
			return false;
		}
		BranchTrek other = (BranchTrek) obj;
		
		Long id = getId();
		Long id2 = other.getId();
		Pack pack = getPack();
		Pack pack2 = other.getPack();
		String key = getKey();
		String key2 = other.getKey();
		Branch branch = getBranch();
		Branch branch2 = other.getBranch();
		
		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (pack != null && !pack.equals(pack2)) {
			return false;
		} else if (pack2 != null) {
			return false;
		}
		
		if (key != null && !key.equals(key2)) {
			return false;
		} else if (key2 != null) {
			return false;
		}
		
		if (branch != null && !branch.equals(branch2)) {
			return false;
		} else if (branch2 != null) {
			return false;
		}
		
		return true;
	}

	@Override
	public final int hashCode() {
		Long id = getId();
		Pack pack = getPack();
		String key = getKey();
		Branch branch = getBranch();

		final int prime = 7;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result	+ (pack != null ? pack.hashCode() : 0);
		result = prime * result	+ (key != null ? key.hashCode() : 0);
		result = prime * result	+ (branch != null ? branch.hashCode() : 0);
		return result;
	}

}
