package org.hypothesis.data.dto;

import java.util.Date;

@SuppressWarnings("serial")
public class EventDto extends EntityDto<Long> {

	private Date timeStamp;
	private Date clientTimeStamp;
	private Long type;
	private String name;
	private String data;
	private Long branchId;
	private Long taskId;
	private Long slideId;
	private long testId;

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Date getClientTimeStamp() {
		return clientTimeStamp;
	}

	public void setClientTimeStamp(Date clientTimeStamp) {
		this.clientTimeStamp = clientTimeStamp;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getSlideId() {
		return slideId;
	}

	public void setSlideId(Long slideId) {
		this.slideId = slideId;
	}

	public long getTestId() {
		return testId;
	}

	public void setTestId(long testId) {
		this.testId = testId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((branchId == null) ? 0 : branchId.hashCode());
		result = prime * result + ((clientTimeStamp == null) ? 0 : clientTimeStamp.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((slideId == null) ? 0 : slideId.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		result = prime * result + (int) (testId ^ (testId >>> 32));
		result = prime * result + ((timeStamp == null) ? 0 : timeStamp.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventDto other = (EventDto) obj;
		if (branchId == null) {
			if (other.branchId != null)
				return false;
		} else if (!branchId.equals(other.branchId))
			return false;
		if (clientTimeStamp == null) {
			if (other.clientTimeStamp != null)
				return false;
		} else if (!clientTimeStamp.equals(other.clientTimeStamp))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (slideId == null) {
			if (other.slideId != null)
				return false;
		} else if (!slideId.equals(other.slideId))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		if (testId != other.testId)
			return false;
		if (timeStamp == null) {
			if (other.timeStamp != null)
				return false;
		} else if (!timeStamp.equals(other.timeStamp))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EventDto [id=" + getId() + ", timeStamp=" + timeStamp + ", clientTimeStamp=" + clientTimeStamp
				+ ", type=" + type + ", name=" + name + ", data=" + data + ", branchId=" + branchId + ", taskId="
				+ taskId + ", slideId=" + slideId + ", testId=" + testId + "]";
	}

}
