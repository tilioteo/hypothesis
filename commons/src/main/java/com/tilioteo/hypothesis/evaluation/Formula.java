/**
 * 
 */
package com.tilioteo.hypothesis.evaluation;

import java.io.Serializable;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface Formula extends Serializable {

	public boolean evaluate(Object input);

}
