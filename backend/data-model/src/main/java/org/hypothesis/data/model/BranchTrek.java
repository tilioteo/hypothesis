package org.hypothesis.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Index;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.HasId;
import org.hypothesis.data.interfaces.TableConstants;

@SuppressWarnings({ "serial", "deprecation" })
@Entity
@Table(name = TableConstants.BRANCH_TREK_TABLE, uniqueConstraints = {
		@UniqueConstraint(columnNames = { FieldConstants.PACK_ID, FieldConstants.KEY, FieldConstants.BRANCH_ID }) })
@org.hibernate.annotations.Table(appliesTo = TableConstants.BRANCH_TREK_TABLE, indexes = {
		@Index(name = "IX_PACK_BRANCH", columnNames = { FieldConstants.PACK_ID, FieldConstants.BRANCH_ID }) })
@Access(AccessType.PROPERTY)
public class BranchTrek implements Serializable, HasId<Long> {

	private Long id;

	private long packId;

	private String key;

	private long branchId;

	private long nextBranchId;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.BRANCH_TREK_GENERATOR)
	@SequenceGenerator(name = TableConstants.BRANCH_TREK_GENERATOR, sequenceName = TableConstants.BRANCH_TREK_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = FieldConstants.PACK_ID, nullable = false)
	public long getPackId() {
		return packId;
	}

	public void setPackId(long packId) {
		this.packId = packId;
	}

	@Column(name = FieldConstants.KEY, nullable = false)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Column(name = FieldConstants.BRANCH_ID, nullable = false)
	public long getBranchId() {
		return branchId;
	}

	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}

	@Column(name = FieldConstants.NEXT_BRANCH_ID, nullable = false)
	public long getNextBranchId() {
		return nextBranchId;
	}

	public void setNextBranchId(long branchId) {
		this.nextBranchId = branchId;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BranchTrek == false)
			return false;

		final BranchTrek other = (BranchTrek) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(getPackId(), other.getPackId()))
			return false;
		if (!Objects.equals(getKey(), other.getKey()))
			return false;
		if (!Objects.equals(getBranchId(), other.getBranchId()))
			return false;
		if (!Objects.equals(getNextBranchId(), other.getNextBranchId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BranchTrek [id=" + id + ", packId=" + packId + ", key=" + key + ", branchId=" + branchId
				+ ", nextBranchId=" + nextBranchId + "]";
	}

}
