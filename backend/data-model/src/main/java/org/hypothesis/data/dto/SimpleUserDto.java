package org.hypothesis.data.dto;

import java.util.Date;
import java.util.Set;

@SuppressWarnings("serial")
public class SimpleUserDto extends EntityDto<Long> {

	private String username;
	private Boolean enabled;
	private boolean autoDisable;
	private boolean testingSuspended;
	private Date expireDate;
	private Set<RoleDto> roles;
	private Set<GroupDto> groups;

	public SimpleUserDto() {
	};

	public SimpleUserDto(String username) {
		this();

		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isAutoDisable() {
		return autoDisable;
	}

	public void setAutoDisable(boolean autoDisable) {
		this.autoDisable = autoDisable;
	}

	public boolean isTestingSuspended() {
		return testingSuspended;
	}

	public void setTestingSuspended(boolean testingSuspended) {
		this.testingSuspended = testingSuspended;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public Set<RoleDto> getRoles() {
		return roles;
	}

	public void setRoles(Set<RoleDto> roles) {
		this.roles = roles;
	}

	public Set<GroupDto> getGroups() {
		return groups;
	}

	public void setGroups(Set<GroupDto> groups) {
		this.groups = groups;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (autoDisable ? 1231 : 1237);
		result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
		result = prime * result + ((expireDate == null) ? 0 : expireDate.hashCode());
		result = prime * result + ((groups == null) ? 0 : groups.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + (testingSuspended ? 1231 : 1237);
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleUserDto other = (SimpleUserDto) obj;
		if (autoDisable != other.autoDisable)
			return false;
		if (enabled == null) {
			if (other.enabled != null)
				return false;
		} else if (!enabled.equals(other.enabled))
			return false;
		if (expireDate == null) {
			if (other.expireDate != null)
				return false;
		} else if (!expireDate.equals(other.expireDate))
			return false;
		if (groups == null) {
			if (other.groups != null)
				return false;
		} else if (!groups.equals(other.groups))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		if (testingSuspended != other.testingSuspended)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimpleUserDto [id=" + getId() + ", username=" + username + ", enabled=" + enabled + ", autoDisable="
				+ autoDisable + ", testingSuspended=" + testingSuspended + ", expireDate=" + expireDate + ", roles="
				+ roles + ", groups=" + groups + "]";
	}

}
