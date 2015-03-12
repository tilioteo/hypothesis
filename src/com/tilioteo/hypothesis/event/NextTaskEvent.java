/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Task;
import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class NextTaskEvent extends AbstractRunningEvent {

	public NextTaskEvent(Task task) {
		this(task, null);
	}

	public NextTaskEvent(Task task, ErrorHandler errorHandler) {
		super(task, errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.NextTask;
	}

	public Task getTask() {
		return (Task) getSource();
	}

}
