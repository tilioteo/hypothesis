/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business.data;

import java.util.Date;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class UserTestState {

	private Long packId;
	private String packName;
	private String packDescription;

	private String branchName;
	private String branchDescription;

	private String taskName;
	private String taskDescription;

	private String slideName;
	private String slideDescription;

	private String eventName;
	private Date eventTime;

	public synchronized Long getPackId() {
		return packId;
	}

	public synchronized void setPackId(Long packId) {
		this.packId = packId;
	}

	public synchronized String getPackName() {
		return packName;
	}

	public synchronized void setPackName(String packName) {
		this.packName = packName;
	}

	public synchronized String getPackDescription() {
		return packDescription;
	}

	public synchronized void setPackDescription(String packDescription) {
		this.packDescription = packDescription;
	}

	public synchronized String getBranchName() {
		return branchName;
	}

	public synchronized void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public synchronized String getBranchDescription() {
		return branchDescription;
	}

	public synchronized void setBranchDescription(String branchDescription) {
		this.branchDescription = branchDescription;
	}

	public synchronized String getTaskName() {
		return taskName;
	}

	public synchronized void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public synchronized String getTaskDescription() {
		return taskDescription;
	}

	public synchronized void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public synchronized String getSlideName() {
		return slideName;
	}

	public synchronized void setSlideName(String slideName) {
		this.slideName = slideName;
	}

	public synchronized String getSlideDescription() {
		return slideDescription;
	}

	public synchronized void setSlideDescription(String slideDescription) {
		this.slideDescription = slideDescription;
	}

	public synchronized String getEventName() {
		return eventName;
	}

	public synchronized void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public synchronized Date getEventTime() {
		return eventTime;
	}

	public synchronized void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

}
