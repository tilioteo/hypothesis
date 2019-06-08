package org.hypothesis.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.interfaces.HasId;
import org.hypothesis.data.interfaces.TableConstants;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 *         Database entity for token which holds information to run test in
 *         isolated application
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = TableConstants.TOKEN_TABLE)
@Access(AccessType.PROPERTY)
public class Token implements Serializable, HasId<String> {

	private String id;

	/**
	 * run test in production or testing mode
	 */
	private boolean production;

	private Long userId;
	private long packId;

	private String viewUid;

	/**
	 * timestamp of request, old tokens are garbaged
	 */
	private Date datetime;

	@Override
	@Id
	@Column(name = FieldConstants.UID)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = FieldConstants.PRODUCTION, nullable = false)
	public boolean isProduction() {
		return production;
	}

	public void setProduction(Boolean production) {
		this.production = production != null ? production : false;
	}

	@Column(name = FieldConstants.USER_ID)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = FieldConstants.PACK_ID, nullable = false)
	public long getPackId() {
		return packId;
	}

	public void setPackId(long packId) {
		this.packId = packId;
	}

	@Column(name = FieldConstants.VIEW_UID)
	public String getViewUid() {
		return viewUid;
	}

	public void setViewUid(String viewUid) {
		this.viewUid = viewUid;
	}

	@Column(name = FieldConstants.DATETIME)
	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Token == false)
			return false;

		final Token other = (Token) obj;
		if (getId() != null || other.getId() != null)
			return Objects.equals(getId(), other.getId());
		if (!Objects.equals(isProduction(), other.isProduction()))
			return false;
		if (!Objects.equals(getUserId(), other.getUserId()))
			return false;
		if (!Objects.equals(getPackId(), other.getPackId()))
			return false;
		if (!Objects.equals(getViewUid(), other.getViewUid()))
			return false;
		if (!Objects.equals(getDatetime(), other.getDatetime()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Token [id=" + id + ", production=" + production + ", userId=" + userId + ", packId=" + packId
				+ ", viewUid=" + viewUid + ", datetime=" + datetime + "]";
	}

}
