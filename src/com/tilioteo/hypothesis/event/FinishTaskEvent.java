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
public class FinishTaskEvent extends AbstractRunningEvent {

	public FinishTaskEvent(Task task) {
		super(task);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.FinishTask;
	}

	public Task getTask() {
		return (Task) getSource();
	}

}
