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

import org.hibernate.annotations.Index;
import org.hypothesis.common.SerializableIdObject;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for branch trek Branch trek is join object for
 *         relation between pack and branch identified by string key
 * 
 */
@Entity
@Table(name = "TBL_BRANCH_TREK", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"PACK_ID", "KEY", "BRANCH_ID" }) })
@org.hibernate.annotations.Table(appliesTo = "TBL_BRANCH_TREK",
		indexes = { @Index(name = "IX_PACK_BRANCH", columnNames = {	"PACK_ID", "BRANCH_ID" }) })
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "branchTrekGenerator")
	@SequenceGenerator(name = "branchTrekGenerator", sequenceName = "hbn_branch_trek_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	public Long getId() {
		return super.getId();
	}

	@ManyToOne
	@JoinColumn(name = "PACK_ID", nullable = false)
	public Pack getPack() {
		return pack;
	}

	protected void setPack(Pack pack) {
		this.pack = pack;
	}

	@Column(name = "KEY", nullable = false)
	public String getKey() {
		return key;
	}

	protected void setKey(String key) {
		this.key = key;
	}

	@ManyToOne
	@JoinColumn(name = "BRANCH_ID", nullable = false)
	public Branch getBranch() {
		return branch;
	}

	protected void setBranch(Branch branch) {
		this.branch = branch;
	}

	@ManyToOne
	@JoinColumn(name = "NEXT_BRANCH_ID", nullable = false)
	public Branch getNextBranch() {
		return nextBranch;
	}

	protected void setNextBranch(Branch branch) {
		this.nextBranch = branch;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BranchTrek))
			return false;
		BranchTrek other = (BranchTrek) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		if (getBranch() == null) {
			if (other.getBranch() != null)
				return false;
		} else if (!getBranch().equals(other.getBranch()))
			return false;
		if (getNextBranch() == null) {
			if (other.getNextBranch() != null)
				return false;
		} else if (!getNextBranch().equals(other.getNextBranch()))
			return false;
		if (getKey() == null) {
			if (other.getKey() != null)
				return false;
		} else if (!getKey().equals(other.getKey()))
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		result = prime * result
				+ ((getBranch() == null) ? 0 : getBranch().hashCode());
		result = prime * result
				+ ((getNextBranch() == null) ? 0 : getNextBranch().hashCode());
		result = prime * result
				+ ((getKey() == null) ? 0 : getKey().hashCode());
		result = prime * result
				+ ((getPack() == null) ? 0 : getPack().hashCode());
		return result;
	}

}
