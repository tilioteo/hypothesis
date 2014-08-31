/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.common.EntityTableConstants;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for token which holds information to run test in
 *         isolated application
 * 
 */
@Entity
@Table(name = EntityTableConstants.TOKEN_TABLE)
@Access(AccessType.PROPERTY)
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
	@Id
	@Column(name = EntityFieldConstants.UID)
	public final String getUid() {
		return uid;
	}

	@Column(name = EntityFieldConstants.PRODUCTION, nullable = false)
	public boolean isProduction() {
		return production;
	}

	public void setProduction(boolean production) {
		this.production = production;
	}

	@ManyToOne(/*cascade = { CascadeType.PERSIST, CascadeType.MERGE }*/)
	@JoinColumn(name = EntityFieldConstants.USER_ID)
	//@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
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

	@Column(name = EntityFieldConstants.DATETIME)
	public final Date getDatetime() {
		return datetime;
	}

	protected void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Token)) {
			return false;
		}
		Token other = (Token) obj;

		String uid = getUid();
		String uid2 = other.getUid();
		boolean production = isProduction();
		boolean production2 = other.isProduction();
		User user = getUser();
		User user2 = other.getUser();
		Pack pack = getPack();
		Pack pack2 = other.getPack();
		Date datetime = getDatetime();
		Date datetime2 = other.getDatetime();

		if (uid != null && !uid.equals(uid2)) {
			return false;
		} else if (uid2 != null) {
			return false;
		}

		if (production != production2) {
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
		
		if (datetime != null && !datetime.equals(datetime2)) {
			return false;
		} else if (datetime2 != null) {
			return false;
		}
		
		return true;
	}

	@Override
	public final int hashCode() {
		String uid = getUid();
		boolean production = isProduction();
		User user = getUser();
		Pack pack = getPack();
		Date datetime = getDatetime();

		final int prime = 59;
		int result = 1;
		result = prime * result	+ (uid != null ? uid.hashCode() : 0);
		result = prime * result	+ (production ? 1 : 0);
		result = prime * result + (user != null ? user.hashCode() : 0);
		result = prime * result	+ (pack != null ? pack.hashCode() : 0);
		result = prime * result	+ (datetime != null ? datetime.hashCode() : 0);
		return result;
	}

}