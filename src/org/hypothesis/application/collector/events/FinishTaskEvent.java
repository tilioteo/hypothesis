/**
 * 
 */
package org.hypothesis.application.collector.events;

import org.hypothesis.entity.Task;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class FinishTaskEvent extends AbstractRunningEvent implements HasName {

	public FinishTaskEvent(Task task) {
		super(task);
	}

	public String getName() {
		return ProcessEvents.FinishTask;
	}

	public Task getTask() {
		return (Task) getSource();
	}

}
