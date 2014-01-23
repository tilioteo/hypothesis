/**
 * 
 */
package com.tilioteo.hypothesis.model;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface Evaluable {

	void evaluate();

	void setVariables(VariableMap variables);

	void updateVariables(VariableMap variables);
}
