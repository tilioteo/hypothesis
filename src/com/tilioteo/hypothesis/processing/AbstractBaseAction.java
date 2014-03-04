/**
 * 
 */
package com.tilioteo.hypothesis.processing;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Base class for action
 * 
 */
public abstract class AbstractBaseAction extends AbstractVariableContainer {

	private String id;
	private Command executeCommand = null;

	protected AbstractBaseAction(HasVariables variables, String id) {
		super(variables);
		this.id = id;
	}
	
	public void setExecuteCommand(Command command) {
		this.executeCommand = command;
	}

	public void execute() {
		if (executeCommand != null) {
			executeCommand.execute();
		}
	}

	public String getId() {
		return id;
	}
}
