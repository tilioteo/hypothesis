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

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for branch trek Branch trek is join object for
 *         relation between pack and branch identified by string key
 * 
 */
@Entity
@Table(name = TableConstants.BRANCH_TREK_TABLE, uniqueConstraints = { @UniqueConstraint(columnNames = {
		FieldConstants.PACK_ID, FieldConstants.KEY, FieldConstants.BRANCH_ID }) })
@org.hibernate.annotations.Table(appliesTo = TableConstants.BRANCH_TREK_TABLE,
		indexes = { @Index(name = "IX_PACK_BRANCH", columnNames = { FieldConstants.PACK_ID, FieldConstants.BRANCH_ID }) })
@Access(AccessType.PROPERTY)
public final class BranchTrek extends SerializableIdObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8262351809120504076L;

	private Pack pack;
	private String key;
	private Branch branch;
	private Branch nextBranch;

	protected BranchTrek() {
		super();
	}

	public BranchTrek(Pack pack, Branch branch, String key, Branch nextBranch) {
		this();
		this.pack = pack;
		this.key = key;
		this.branch = branch;
		this.nextBranch = nextBranch;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.BRANCH_TREK_GENERATOR)
	@SequenceGenerator(name = TableConstants.BRANCH_TREK_GENERATOR, sequenceName = TableConstants.BRANCH_TREK_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return super.getId();
	}

	@ManyToOne
	@JoinColumn(name = FieldConstants.PACK_ID, nullable = false)
	public Pack getPack() {
		return pack;
	}

	protected void setPack(Pack pack) {
		this.pack = pack;
	}

	@Column(name = FieldConstants.KEY, nullable = false)
	public String getKey() {
		return key;
	}

	protected void setKey(String key) {
		this.key = key;
	}

	@ManyToOne
	@JoinColumn(name = FieldConstants.BRANCH_ID, nullable = false)
	public Branch getBranch() {
		return branch;
	}

	protected void setBranch(Branch branch) {
		this.branch = branch;
	}

	@ManyToOne
	@JoinColumn(name = FieldConstants.NEXT_BRANCH_ID, nullable = false)
	public Branch getNextBranch() {
		return nextBranch;
	}

	protected void setNextBranch(Branch branch) {
		this.nextBranch = branch;
	}

	@Override
	public boolean equals(Object obj) {
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
		Branch nextBranch = getNextBranch();
		Branch nextBranch2 = other.getNextBranch();
		
		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (pack == null) {
			if (pack2 != null) {
				return false;
			}
		} else if (!pack.equals(pack2)) {
			return false;
		}
		
		if (key == null) {
			if (key2 != null) {
				return false;
			}
		} else if (!key.equals(key2)) {
			return false;
		}
		
		if (branch == null) {
			if (branch2 != null) {
				return false;
			}
		} else if (!branch.equals(branch2)) {
			return false;
		}
		
		if (nextBranch == null) {
			if (nextBranch2 != null) {
				return false;
			}
		} else if (!nextBranch.equals(nextBranch2)) {
			return false;
		}
		
		return true;
	}

	@Override
	public int hashCode() {
		Long id = getId();
		Pack pack = getPack();
		String key = getKey();
		Branch branch = getBranch();
		Branch nextBranch = getNextBranch();

		final int prime = 7;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result	+ (pack != null ? pack.hashCode() : 0);
		result = prime * result	+ (key != null ? key.hashCode() : 0);
		result = prime * result	+ (branch != null ? branch.hashCode() : 0);
		result = prime * result	+ (nextBranch != null ? nextBranch.hashCode() : 0);
		return result;
	}

}
