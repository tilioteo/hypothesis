/**
 * 
 */
package com.tilioteo.hypothesis.model;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Base class for everything, which can be evaluated
 * 
 * 
 */
public abstract class AbstractVariableContainer {

	private HasVariables variables;

	protected AbstractVariableContainer(HasVariables variables) {
		this.variables = variables;
	}

	protected HasVariables getVariables() {
		return variables;
	}

	protected void setVariables(HasVariables variables) {
		this.variables = variables;
	}
}
