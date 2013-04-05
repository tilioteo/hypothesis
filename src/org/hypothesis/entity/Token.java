/**
 * 
 */
package org.hypothesis.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hypothesis.common.SerializableUidObject;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for token which holds information to run test in
 *         isolated application
 * 
 */
@Entity
@Table(name = "TBL_TOKEN")
public final class Token extends SerializableUidObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4687653172248782423L;

	/**
	 * run test in production or testing mode
	 */
	private boolean production;

	private User user;
	private Pack pack;

	/**
	 * timestamp of request, old tokens are garbaged
	 */
	private Date datetime;

	protected Token() {
		super();
	}

	/**
	 * constructor will generate unique id for token
	 * 
	 * @param user
	 * @param pack
	 */
	public Token(User user, Pack pack) {
		this();
		this.uid = UUID.randomUUID().toString().replaceAll("-", "");
		this.user = user;
		this.pack = pack;
		this.datetime = new Date();
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Token))
			return false;
		Token other = (Token) obj;
		if (getUid() == null) {
			if (other.getUid() != null)
				return false;
		} else if (!getUid().equals(other.getUid()))
			return false;
		// TODO remove when Buffered.SourceException occurs
		if (getDatetime() == null) {
			if (other.getDatetime() != null)
				return false;
		} else if (!getDatetime().equals(other.getDatetime()))
			return false;
		if (getPack() == null) {
			if (other.getPack() != null)
				return false;
		} else if (!getPack().equals(other.getPack()))
			return false;
		if (getUid() == null) {
			if (other.getUid() != null)
				return false;
		} else if (!getUid().equals(other.getUid()))
			return false;
		if (getUser() == null) {
			if (other.getUser() != null)
				return false;
		} else if (!getUser().equals(other.getUser()))
			return false;
		return true;
	}

	public final Date getDatetime() {
		return datetime;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "PACK_ID", nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Pack getPack() {
		return pack;
	}

	@Override
	@Id
	@Column(name = "UID")
	public final String getUid() {
		return uid;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "USER_ID", nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final User getUser() {
		return user;
	}

	@Override
	public final int hashCode() {
		final int prime = 211;
		int result = 1;
		result = prime * result
				+ ((getUid() == null) ? 0 : getUid().hashCode());
		// TODO remove when Buffered.SourceException occurs
		result = prime * result
				+ ((getDatetime() == null) ? 0 : getDatetime().hashCode());
		result = prime * result
				+ ((getPack() == null) ? 0 : getPack().hashCode());
		result = prime * result
				+ ((getUid() == null) ? 0 : getUid().hashCode());
		result = prime * result
				+ ((getUser() == null) ? 0 : getUser().hashCode());
		return result;
	}

	@Column(name = "PRODUCTION", nullable = false)
	public boolean isProduction() {
		return production;
	}

	protected void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	protected void setPack(Pack pack) {
		this.pack = pack;
	}

	public void setProduction(boolean production) {
		this.production = production;
	}

	protected void setUser(User user) {
		this.user = user;
	}

}
