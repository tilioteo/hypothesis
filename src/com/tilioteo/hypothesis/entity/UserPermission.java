/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
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

import org.hibernate.annotations.Cascade;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for relation between user and pack
 * 
 */
@Entity
@Table(name = "TBL_USER_PERMITION", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"USER_ID", "PACK_ID" }) })
@Access(AccessType.PROPERTY)
public final class UserPermission extends SerializableIdObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8331070869109730350L;

	private User user;
	private Pack pack;
	/**
	 * explicit enable or disable this pack for user
	 */
	private Boolean enabled;

	/**
	 * enable number of pass through this pack default value is 1, because more
	 * repetitions of one pack will devaluate the measurement
	 * 
	 */
	private Integer pass; // default 1, null - unlimited

	protected UserPermission() {
		super();
	}

	public UserPermission(User user, Pack pack) {
		this(user, pack, true);
	}

	public UserPermission(User user, Pack pack, boolean enabled) {
		this(user, pack, enabled, 1);
	}

	public UserPermission(User user, Pack pack, boolean enabled, Integer pass) {
		this();
		this.user = user;
		this.pack = pack;
		this.enabled = enabled;
		this.pass = pass;
	}

	public UserPermission(User user, Pack pack, Integer pass) {
		this(user, pack, true, pass);
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userPermitionGenerator")
	@SequenceGenerator(name = "userPermitionGenerator", sequenceName = "hbn_user_permition_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	public final Long getId() {
		return super.getId();
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "USER_ID", nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final User getUser() {
		return user;
	}

	protected void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "PACK_ID", nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Pack getPack() {
		return pack;
	}

	protected void setPack(Pack pack) {
		this.pack = pack;
	}

	@Column(name = "ENABLED", nullable = false)
	public final Boolean getEnabled() {
		return enabled;
	}

	public final void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = "PASS")
	public final Integer getPass() {
		return pass;
	}

	public final void setPass(Integer pass) {
		this.pass = pass;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UserPermission))
			return false;
		UserPermission other = (UserPermission) obj;
		/*
		 * if (getId() == null) { if (other.getId() != null) return false; }
		 * else if (!getId().equals(other.getId())) return false;
		 */
		// TODO remove when Buffered.SourceException occurs and use ID
		if (getEnabled() == null) {
			if (other.getEnabled() != null)
				return false;
		} else if (!getEnabled().equals(other.getEnabled()))
			return false;
		if (getPack() == null) {
			if (other.getPack() != null)
				return false;
		} else if (!getPack().equals(other.getPack()))
			return false;
		if (getPass() == null) {
			if (other.getPass() != null)
				return false;
		} else if (!getPass().equals(other.getPass()))
			return false;
		if (getUser() == null) {
			if (other.getUser() != null)
				return false;
		} else if (!getUser().equals(other.getUser()))
			return false;
		return true;
	}

	@Override
	public final int hashCode() {
		final int prime = 199;
		int result = 1;
		// result = prime * result + ((getId() == null) ? 0 :
		// getId().hashCode());
		// TODO remove when Buffered.SourceException occurs and use ID
		result = prime * result
				+ ((getEnabled() == null) ? 0 : getEnabled().hashCode());
		result = prime * result
				+ ((getPack() == null) ? 0 : getPack().hashCode());
		result = prime * result
				+ ((getPass() == null) ? 0 : getPass().hashCode());
		result = prime * result
				+ ((getUser() == null) ? 0 : getUser().hashCode());
		return result;
	}

}
