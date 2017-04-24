/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.evaluation;

import org.hypothesis.interfaces.HasVariables;

import java.util.Map;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractVariableContainer implements HasVariables {

	private HasVariables variables;

	protected AbstractVariableContainer(HasVariables variables) {
		this.variables = variables;
	}

	@Override
	public Map<String, org.hypothesis.interfaces.Variable<?>> getVariables() {
		return variables != null ? variables.getVariables() : null;
	}

	protected void setVariables(HasVariables variables) {
		this.variables = variables;
	}
}
