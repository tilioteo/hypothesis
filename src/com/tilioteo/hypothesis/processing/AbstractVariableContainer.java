/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import java.util.Map;

import com.tilioteo.hypothesis.interfaces.HasVariables;
import com.tilioteo.hypothesis.interfaces.Variable;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Base class for everything, which can be evaluated
 * 
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractVariableContainer implements HasVariables {

	private HasVariables variables;

	protected AbstractVariableContainer(HasVariables variables) {
		this.variables = variables;
	}

	public Map<String, Variable<?>> getVariables() {
		return variables != null ? variables.getVariables() : null;
	}

	protected void setVariables(HasVariables variables) {
		this.variables = variables;
	}
}
