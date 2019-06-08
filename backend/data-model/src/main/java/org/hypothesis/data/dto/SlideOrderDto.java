package org.hypothesis.data.dto;

import java.util.List;

@SuppressWarnings("serial")
public class SlideOrderDto extends EntityDto<Long> {

	private long testId;

	private long taskId;

	private List<Integer> order;

	public long getTestId() {
		return testId;
	}

	public void setTestId(long testId) {
		this.testId = testId;
	}

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public List<Integer> getOrder() {
		return order;
	}

	public void setOrder(List<Integer> order) {
		this.order = order;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((order == null) ? 0 : order.hashCode());
		result = prime * result + (int) (taskId ^ (taskId >>> 32));
		result = prime * result + (int) (testId ^ (testId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SlideOrderDto other = (SlideOrderDto) obj;
		if (order == null) {
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;
		if (taskId != other.taskId)
			return false;
		if (testId != other.testId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SlideOrderDto [id=" + getId() + "testId=" + testId + ", taskId=" + taskId + ", order=" + order + "]";
	}

}
