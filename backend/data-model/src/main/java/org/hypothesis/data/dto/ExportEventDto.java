package org.hypothesis.data.dto;

import java.util.Date;

@SuppressWarnings("serial")
public class ExportEventDto extends EntityDto<Long> {

	private Date timeStamp;
	private Date clientTimeStamp;
	private Long type;
	private String name;
	private String data;
	private Long branchId;
	private String branchName;
	private Long taskId;
	private String taskName;
	private Long slideId;
	private String slideName;
	private Long testId;
	private Long userId;
	private Long packId;
	private String packName;
	private Date created;

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

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Long getSlideId() {
		return slideId;
	}

	public void setSlideId(Long slideId) {
		this.slideId = slideId;
	}

	public String getSlideName() {
		return slideName;
	}

	public void setSlideName(String slideName) {
		this.slideName = slideName;
	}

	public Long getTestId() {
		return testId;
	}

	public void setTestId(Long testId) {
		this.testId = testId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getPackId() {
		return packId;
	}

	public void setPackId(Long packId) {
		this.packId = packId;
	}

	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((branchId == null) ? 0 : branchId.hashCode());
		result = prime * result + ((branchName == null) ? 0 : branchName.hashCode());
		result = prime * result + ((clientTimeStamp == null) ? 0 : clientTimeStamp.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((packId == null) ? 0 : packId.hashCode());
		result = prime * result + ((packName == null) ? 0 : packName.hashCode());
		result = prime * result + ((slideId == null) ? 0 : slideId.hashCode());
		result = prime * result + ((slideName == null) ? 0 : slideName.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		result = prime * result + ((taskName == null) ? 0 : taskName.hashCode());
		result = prime * result + ((testId == null) ? 0 : testId.hashCode());
		result = prime * result + ((timeStamp == null) ? 0 : timeStamp.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExportEventDto other = (ExportEventDto) obj;
		if (branchId == null) {
			if (other.branchId != null)
				return false;
		} else if (!branchId.equals(other.branchId))
			return false;
		if (branchName == null) {
			if (other.branchName != null)
				return false;
		} else if (!branchName.equals(other.branchName))
			return false;
		if (clientTimeStamp == null) {
			if (other.clientTimeStamp != null)
				return false;
		} else if (!clientTimeStamp.equals(other.clientTimeStamp))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
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
		if (packId == null) {
			if (other.packId != null)
				return false;
		} else if (!packId.equals(other.packId))
			return false;
		if (packName == null) {
			if (other.packName != null)
				return false;
		} else if (!packName.equals(other.packName))
			return false;
		if (slideId == null) {
			if (other.slideId != null)
				return false;
		} else if (!slideId.equals(other.slideId))
			return false;
		if (slideName == null) {
			if (other.slideName != null)
				return false;
		} else if (!slideName.equals(other.slideName))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		if (taskName == null) {
			if (other.taskName != null)
				return false;
		} else if (!taskName.equals(other.taskName))
			return false;
		if (testId == null) {
			if (other.testId != null)
				return false;
		} else if (!testId.equals(other.testId))
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
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExportEventDto [id=" + getId() + ", timeStamp=" + timeStamp + ", clientTimeStamp=" + clientTimeStamp
				+ ", type=" + type + ", name=" + name + ", data=" + data + ", branchId=" + branchId + ", branchName="
				+ branchName + ", taskId=" + taskId + ", taskName=" + taskName + ", slideId=" + slideId + ", slideName="
				+ slideName + ", testId=" + testId + ", userId=" + userId + ", packId=" + packId + ", packName="
				+ packName + ", created=" + created + "]";
	}

}
