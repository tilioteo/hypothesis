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

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.HasId;
import org.hypothesis.data.interfaces.TableConstants;

@SuppressWarnings("serial")
@Entity
@Table(name = TableConstants.GROUP_PERMISSION_TABLE, uniqueConstraints = {
		@UniqueConstraint(columnNames = { FieldConstants.GROUP_ID, FieldConstants.PACK_ID }) })
@Access(AccessType.PROPERTY)
public class GroupPermission implements Serializable, HasId<Long> {

	private Long id;

	private long groupId;

	private long packId;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.GROUP_PERMISSION_GENERATOR)
	@SequenceGenerator(name = TableConstants.GROUP_PERMISSION_GENERATOR, sequenceName = TableConstants.GROUP_PERMISSION_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = FieldConstants.GROUP_ID, nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	@Column(name = FieldConstants.PACK_ID, nullable = false)
	public long getPackId() {
		return packId;
	}

	public void setPackId(long packId) {
		this.packId = packId;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GroupPermission == false)
			return false;

		final GroupPermission other = (GroupPermission) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(getGroupId(), other.getGroupId()))
			return false;
		if (!Objects.equals(getPackId(), other.getPackId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GroupPermission [id=" + id + ", groupId=" + groupId + ", packId=" + packId + "]";
	}

}
