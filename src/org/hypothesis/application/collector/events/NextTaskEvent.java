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
public class NextTaskEvent extends AbstractRunningEvent implements HasName {

	public NextTaskEvent(Task task) {
		super(task);
	}

	public String getName() {
		return ProcessEvents.NextTask;
	}

	public Task getTask() {
		return (Task) getSource();
	}

}
