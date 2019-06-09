package org.hypothesis.data.dto;

import java.util.Date;
import java.util.List;

import org.hypothesis.data.api.Status;

@SuppressWarnings("serial")
public class TestDto extends EntityDto<Long> {

	private boolean production;
	private Date created;
	private Date started;
	private Date finished;
	private Date broken;
	private Date lastAccess;

	private Status status;
	private UserDto user;
	private PackDto pack;

	private Long lastBranchId;
	private Long lastTaskId;
	private Long lastSlideId;

	private List<EventDto> events;
	private List<ScoreDto> scores;

	public boolean isProduction() {
		return production;
	}

	public void setProduction(boolean production) {
		this.production = production;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getStarted() {
		return started;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

	public Date getFinished() {
		return finished;
	}

	public void setFinished(Date finished) {
		this.finished = finished;
	}

	public Date getBroken() {
		return broken;
	}

	public void setBroken(Date broken) {
		this.broken = broken;
	}

	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public PackDto getPack() {
		return pack;
	}

	public void setPack(PackDto pack) {
		this.pack = pack;
	}

	public Long getLastBranchId() {
		return lastBranchId;
	}

	public void setLastBranchId(Long lastBranchId) {
		this.lastBranchId = lastBranchId;
	}

	public Long getLastTaskId() {
		return lastTaskId;
	}

	public void setLastTaskId(Long lastTaskId) {
		this.lastTaskId = lastTaskId;
	}

	public Long getLastSlideId() {
		return lastSlideId;
	}

	public void setLastSlideId(Long lastSlideId) {
		this.lastSlideId = lastSlideId;
	}

	public List<EventDto> getEvents() {
		return events;
	}

	public void setEvents(List<EventDto> events) {
		this.events = events;
	}

	public List<ScoreDto> getScores() {
		return scores;
	}

	public void setScores(List<ScoreDto> scores) {
		this.scores = scores;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((broken == null) ? 0 : broken.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((events == null) ? 0 : events.hashCode());
		result = prime * result + ((finished == null) ? 0 : finished.hashCode());
		result = prime * result + ((lastAccess == null) ? 0 : lastAccess.hashCode());
		result = prime * result + ((lastBranchId == null) ? 0 : lastBranchId.hashCode());
		result = prime * result + ((lastSlideId == null) ? 0 : lastSlideId.hashCode());
		result = prime * result + ((lastTaskId == null) ? 0 : lastTaskId.hashCode());
		result = prime * result + ((pack == null) ? 0 : pack.hashCode());
		result = prime * result + (production ? 1231 : 1237);
		result = prime * result + ((scores == null) ? 0 : scores.hashCode());
		result = prime * result + ((started == null) ? 0 : started.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestDto other = (TestDto) obj;
		if (broken == null) {
			if (other.broken != null)
				return false;
		} else if (!broken.equals(other.broken))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (events == null) {
			if (other.events != null)
				return false;
		} else if (!events.equals(other.events))
			return false;
		if (finished == null) {
			if (other.finished != null)
				return false;
		} else if (!finished.equals(other.finished))
			return false;
		if (lastAccess == null) {
			if (other.lastAccess != null)
				return false;
		} else if (!lastAccess.equals(other.lastAccess))
			return false;
		if (lastBranchId == null) {
			if (other.lastBranchId != null)
				return false;
		} else if (!lastBranchId.equals(other.lastBranchId))
			return false;
		if (lastSlideId == null) {
			if (other.lastSlideId != null)
				return false;
		} else if (!lastSlideId.equals(other.lastSlideId))
			return false;
		if (lastTaskId == null) {
			if (other.lastTaskId != null)
				return false;
		} else if (!lastTaskId.equals(other.lastTaskId))
			return false;
		if (pack == null) {
			if (other.pack != null)
				return false;
		} else if (!pack.equals(other.pack))
			return false;
		if (production != other.production)
			return false;
		if (scores == null) {
			if (other.scores != null)
				return false;
		} else if (!scores.equals(other.scores))
			return false;
		if (started == null) {
			if (other.started != null)
				return false;
		} else if (!started.equals(other.started))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TestDto [id=" + getId() + ", production=" + production + ", created=" + created + ", started=" + started
				+ ", finished=" + finished + ", broken=" + broken + ", lastAccess=" + lastAccess + ", status=" + status
				+ ", user=" + user + ", pack=" + pack + ", lastBranchId=" + lastBranchId + ", lastTaskId=" + lastTaskId
				+ ", lastSlideId=" + lastSlideId + ", events=" + events + ", scores=" + scores + "]";
	}

}
