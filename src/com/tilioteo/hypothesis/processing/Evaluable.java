/**
 * 
 */
package com.tilioteo.hypothesis.processing;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface Evaluable {

	void evaluate();

	void setVariables(VariableMap variables);

	void updateVariables(VariableMap variables);
}
