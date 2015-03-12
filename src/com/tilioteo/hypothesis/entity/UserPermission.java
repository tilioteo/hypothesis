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

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.common.EntityTableConstants;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for relation between user and pack
 * 
 */
@Entity
@Table(name = EntityTableConstants.USER_PERMISSION_TABLE, uniqueConstraints = { @UniqueConstraint(columnNames = {
		EntityFieldConstants.USER_ID, EntityFieldConstants.PACK_ID }) })
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EntityTableConstants.USER_PERMISSION_GENERATOR)
	@SequenceGenerator(name = EntityTableConstants.USER_PERMISSION_GENERATOR, sequenceName = EntityTableConstants.USER_PERMISSION_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = EntityFieldConstants.ID)
	public final Long getId() {
		return super.getId();
	}

	@ManyToOne
	@JoinColumn(name = EntityFieldConstants.USER_ID, nullable = false)
	public final User getUser() {
		return user;
	}

	protected void setUser(User user) {
		this.user = user;
	}

	@ManyToOne
	@JoinColumn(name = EntityFieldConstants.PACK_ID, nullable = false)
	public final Pack getPack() {
		return pack;
	}

	protected void setPack(Pack pack) {
		this.pack = pack;
	}

	@Column(name = EntityFieldConstants.ENABLED, nullable = false)
	public final Boolean getEnabled() {
		return enabled;
	}

	public final void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = EntityFieldConstants.PASS)
	public final Integer getPass() {
		return pass;
	}

	public final void setPass(Integer pass) {
		this.pass = pass;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof UserPermission)) {
			return false;
		}
		UserPermission other = (UserPermission) obj;

		Long id = getId();
		Long id2 = other.getId();
		User user = getUser();
		User user2 = other.getUser();
		Pack pack = getPack();
		Pack pack2 = other.getPack();
		Boolean enabled = getEnabled();
		Boolean enabled2 = other.getEnabled();
		Integer pass = getPass();
		Integer pass2 = other.getPass();
		
		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (user != null && !user.equals(user2)) {
			return false;
		} else if (user2 != null) {
			return false;
		}
		
		if (pack != null && !pack.equals(pack2)) {
			return false;
		} else if (pack2 != null) {
			return false;
		}
		
		if (enabled != null && !enabled.equals(enabled2)) {
			return false;
		} else if (enabled2 != null) {
			return false;
		}
		
		if (pass != null && !pass.equals(pass2)) {
			return false;
		} else if (pass2 != null) {
			return false;
		}

		return true;
	}

	@Override
	public final int hashCode() {
		Long id = getId();
		User user = getUser();
		Pack pack = getPack();
		Boolean enabled = getEnabled();
		Integer pass = getPass();

		final int prime = 67;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result	+ (user != null ? user.hashCode() : 0);
		result = prime * result	+ (pack != null ? pack.hashCode() : 0);
		result = prime * result	+ (enabled != null ? enabled.hashCode() : 0);
		result = prime * result + (pass != null ? pass.hashCode() : 0);
		return result;
	}

}
