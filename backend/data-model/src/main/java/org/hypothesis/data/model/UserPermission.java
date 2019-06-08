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
@Table(name = TableConstants.USER_PERMISSION_TABLE, uniqueConstraints = {
		@UniqueConstraint(columnNames = { FieldConstants.USER_ID, FieldConstants.PACK_ID }) })
@Access(AccessType.PROPERTY)
public class UserPermission implements Serializable, HasId<Long> {

	private Long id;

	private long userId;

	private long packId;

	/**
	 * explicit enable or disable this pack for user
	 */
	private boolean enabled;

	/**
	 * enable number of pass through this pack default value is 1, because more
	 * repetitions of one pack will devaluate the measurement
	 * 
	 */
	private Integer pass; // default 1, null - unlimited

	private Integer rank;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.USER_PERMISSION_GENERATOR)
	@SequenceGenerator(name = TableConstants.USER_PERMISSION_GENERATOR, sequenceName = TableConstants.USER_PERMISSION_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = FieldConstants.USER_ID, nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	@Column(name = FieldConstants.PACK_ID, nullable = false)
	public long getPackId() {
		return packId;
	}

	public void setPackId(long packId) {
		this.packId = packId;
	}

	@Column(name = FieldConstants.ENABLED, nullable = false)
	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = FieldConstants.PASS)
	public Integer getPass() {
		return pass;
	}

	public void setPass(Integer pass) {
		this.pass = pass;
	}

	@Column(name = FieldConstants.RANK, nullable = false)
	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserPermission == false)
			return false;

		final UserPermission other = (UserPermission) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(getUserId(), other.getUserId()))
			return false;
		if (!Objects.equals(getPackId(), other.getPackId()))
			return false;
		if (!Objects.equals(getEnabled(), other.getEnabled()))
			return false;
		if (!Objects.equals(getPass(), other.getPass()))
			return false;
		if (!Objects.equals(getRank(), other.getRank()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserPermission [id=" + id + ", userId=" + userId + ", packId=" + packId + ", enabled=" + enabled
				+ ", pass=" + pass + ", rank=" + rank + "]";
	}

}
