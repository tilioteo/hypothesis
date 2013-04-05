/**
 * 
 */
package org.hypothesis.application.collector.evaluable;

import org.hypothesis.application.collector.slide.VariableMap;


/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface Evaluable {

	void evaluate();

	void setVariables(VariableMap variables);

	void updateVariables(VariableMap variables);
}
