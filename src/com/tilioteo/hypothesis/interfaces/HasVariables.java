/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.util.Map;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         interface for classes which provide variables
 * 
 */
public interface HasVariables {

	Map<String, Variable<?>> getVariables();

}
