/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import com.tilioteo.hypothesis.interfaces.Action;
import com.tilioteo.hypothesis.interfaces.HasVariables;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Base class for action
 * 
 */
public abstract class AbstractBaseAction extends AbstractVariableContainer implements Action {

	private String id;
	private Command executeCommand = null;

	protected AbstractBaseAction(HasVariables variables, String id) {
		super(variables);
		this.id = id;
	}
	
	public void setExecuteCommand(Command command) {
		this.executeCommand = command;
	}

	@Override
	public void execute() {
		Command.Executor.execute(executeCommand);
	}

	public String getId() {
		return id;
	}
}
