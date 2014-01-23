/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Task;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class NextTaskEvent extends AbstractRunningEvent {

	public NextTaskEvent(Task task) {
		super(task);
	}

	public String getName() {
		return ProcessEventTypes.NextTask;
	}

	public Task getTask() {
		return (Task) getSource();
	}

}
