/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.evaluation;

import org.hypothesis.interfaces.Command;
import org.hypothesis.interfaces.HasVariables;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractBaseAction extends AbstractVariableContainer implements org.hypothesis.interfaces.Action {

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
