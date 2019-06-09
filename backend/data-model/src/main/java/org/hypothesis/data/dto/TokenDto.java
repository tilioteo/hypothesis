/**
 * 
 */
package org.hypothesis.data.dto;

/**
 * @author morongk
 *
 */
@SuppressWarnings("serial")
public class TokenDto extends EntityDto<String> {

	private boolean production;
	private long packId;
	private String viewUid;
	private SimpleUserDto user;

	public boolean isProduction() {
		return production;
	}

	public void setProduction(boolean production) {
		this.production = production;
	}

	public long getPackId() {
		return packId;
	}

	public void setPackId(long packId) {
		this.packId = packId;
	}

	public String getViewUid() {
		return viewUid;
	}

	public void setViewUid(String viewUid) {
		this.viewUid = viewUid;
	}

	public SimpleUserDto getUser() {
		return user;
	}

	public void setUser(SimpleUserDto user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (packId ^ (packId >>> 32));
		result = prime * result + (production ? 1231 : 1237);
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + ((viewUid == null) ? 0 : viewUid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}

		if (getClass() != obj.getClass())
			return false;
		TokenDto other = (TokenDto) obj;
		if (packId != other.packId)
			return false;
		if (production != other.production)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (viewUid == null) {
			if (other.viewUid != null)
				return false;
		} else if (!viewUid.equals(other.viewUid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TokenDto [id=" + getId() + ", production=" + production + ", packId=" + packId + ", viewUid=" + viewUid
				+ ", user=" + user + "]";
	}

}
