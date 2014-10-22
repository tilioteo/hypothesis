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
public class FinishTaskEvent extends AbstractRunningEvent {

	public FinishTaskEvent(Task task) {
		this(task, null);
	}

	public FinishTaskEvent(Task task, ErrorHandler errorHandler) {
		super(task, errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.FinishTask;
	}

	public Task getTask() {
		return (Task) getSource();
	}

}
