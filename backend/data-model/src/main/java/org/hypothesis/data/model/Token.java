/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.model;

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

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 *         Database entity for token which holds information to run test in
 *         isolated application
 * 
 */
@Entity
@Table(name = TableConstants.TOKEN_TABLE)
@Access(AccessType.PROPERTY)
public final class Token extends SerializableEntity<String> {

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

	private String viewUid;

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
	public Token(User user, Pack pack, String viewUid) {
		this();
		this.id = UUID.randomUUID().toString().replaceAll("-", "");
		this.user = user;
		this.pack = pack;
		this.viewUid = viewUid;
		this.datetime = new Date();
	}

	@Override
	@Id
	@Column(name = FieldConstants.UID)
	public String getId() {
		return id;
	}

	@Column(name = FieldConstants.PRODUCTION, nullable = false)
	public boolean isProduction() {
		return production;
	}

	public void setProduction(Boolean production) {
		this.production = production != null ? production : false;
	}

	@ManyToOne(/* cascade = { CascadeType.PERSIST, CascadeType.MERGE } */)
	@JoinColumn(name = FieldConstants.USER_ID)
	// @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
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

	@Column(name = FieldConstants.VIEW_UID)
	public String getViewUid() {
		return viewUid;
	}

	protected void setViewUid(String viewUid) {
		this.viewUid = viewUid;
	}

	@Column(name = FieldConstants.DATETIME)
	public Date getDatetime() {
		return datetime;
	}

	protected void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	@Override
	public boolean equals(Object obj) {
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

		String id = getId();
		String id2 = other.getId();
		boolean production = isProduction();
		boolean production2 = other.isProduction();
		User user = getUser();
		User user2 = other.getUser();
		Pack pack = getPack();
		Pack pack2 = other.getPack();
		Date datetime = getDatetime();
		Date datetime2 = other.getDatetime();

		if (id == null) {
			if (id2 != null) {
				return false;
			}
		} else if (!id.equals(id2)) {
			return false;
		}

		if (production != production2) {
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

		if (datetime == null) {
			if (datetime2 != null) {
				return false;
			}
		} else if (!datetime.equals(datetime2)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		String id = getId();
		boolean production = isProduction();
		User user = getUser();
		Pack pack = getPack();
		Date datetime = getDatetime();

		final int prime = 59;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result + (production ? 1 : 0);
		result = prime * result + (user != null ? user.hashCode() : 0);
		result = prime * result + (pack != null ? pack.hashCode() : 0);
		result = prime * result + (datetime != null ? datetime.hashCode() : 0);
		return result;
	}

}
