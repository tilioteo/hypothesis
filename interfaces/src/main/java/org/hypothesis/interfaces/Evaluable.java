/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface Evaluable extends Serializable {

	void evaluate();

	void setVariables(Map<String, Variable<?>> variables);

	void updateVariables(Map<String, Variable<?>> variables);
}
