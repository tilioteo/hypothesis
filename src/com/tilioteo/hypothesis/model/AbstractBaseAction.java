/**
 * 
 */
package com.tilioteo.hypothesis.model;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Base class for action
 * 
 */
public abstract class AbstractBaseAction extends AbstractVariableContainer {

	private String id;

	protected AbstractBaseAction(HasVariables variables, String id) {
		super(variables);
		this.id = id;
	}

	public abstract void execute();

	public String getId() {
		return id;
	}
}
