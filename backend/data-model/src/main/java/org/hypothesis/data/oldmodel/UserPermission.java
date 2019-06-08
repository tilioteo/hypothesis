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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.TableConstants;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@Entity
@Table(name = TableConstants.USER_PERMISSION_TABLE, uniqueConstraints = {
		@UniqueConstraint(columnNames = { FieldConstants.USER_ID, FieldConstants.PACK_ID }) })
@Access(AccessType.PROPERTY)
@Deprecated
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
	
	private Integer rank;

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
		this(user, pack, enabled, pass, 0);
	}

	public UserPermission(User user, Pack pack, boolean enabled, Integer pass, Integer rank) {
		this();
		this.user = user;
		this.pack = pack;
		this.enabled = enabled;
		this.pass = pass;
		this.rank = rank;
	}

	public UserPermission(User user, Pack pack, Integer pass) {
		this(user, pack, true, pass);
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.USER_PERMISSION_GENERATOR)
	@SequenceGenerator(name = TableConstants.USER_PERMISSION_GENERATOR, sequenceName = TableConstants.USER_PERMISSION_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return super.getId();
	}

	@ManyToOne
	@JoinColumn(name = FieldConstants.USER_ID, nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	public User getUser() {
		return user;
	}

	protected void setUser(User user) {
		this.user = user;
	}

	@ManyToOne
	@JoinColumn(name = FieldConstants.PACK_ID, nullable = false)
	public Pack getPack() {
		return pack;
	}

	protected void setPack(Pack pack) {
		this.pack = pack;
	}

	@Column(name = FieldConstants.ENABLED, nullable = false)
	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
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
	public boolean equals(Object obj) {
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
		Integer rank = getRank();
		Integer rank2 = other.getRank();

		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (user == null) {
			if (user2 != null) {
				return false;
			}
		} else if (!user.equals(user2)) {
			return false;
		}

		if (pack == null) {
			if (pack2 != null) {
				return false;
			}
		} else if (!pack.equals(pack2)) {
			return false;
		}

		if (enabled == null) {
			if (enabled2 != null) {
				return false;
			}
		} else if (!enabled.equals(enabled2)) {
			return false;
		}

		if (pass == null) {
			if (pass2 != null) {
				return false;
			}
		} else if (!pass.equals(pass2)) {
			return false;
		}

		if (rank == null) {
			if (rank2 != null) {
				return false;
			}
		} else if (!rank.equals(rank2)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		Long id = getId();
		User user = getUser();
		Pack pack = getPack();
		Boolean enabled = getEnabled();
		Integer pass = getPass();
		Integer rank = getRank();

		final int prime = 67;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result + (user != null ? user.hashCode() : 0);
		result = prime * result + (pack != null ? pack.hashCode() : 0);
		result = prime * result + (enabled != null ? enabled.hashCode() : 0);
		result = prime * result + (pass != null ? pass.hashCode() : 0);
		result = prime * result + (rank != null ? rank.hashCode() : 0);
		return result;
	}

}
