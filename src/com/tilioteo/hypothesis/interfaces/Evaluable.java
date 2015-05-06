/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.util.Map;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface Evaluable {

	public void evaluate();

	public void setVariables(Map<String, Variable<?>> variables);

	public void updateVariables(Map<String, Variable<?>> variables);
}
