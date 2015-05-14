/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface Evaluable extends Serializable {

	public void evaluate();

	public void setVariables(Map<String, Variable<?>> variables);

	public void updateVariables(Map<String, Variable<?>> variables);
}
