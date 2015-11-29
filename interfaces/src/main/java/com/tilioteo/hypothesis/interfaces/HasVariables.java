/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         interface for classes which provide variables
 * 
 */
public interface HasVariables extends Serializable {

	Map<String, Variable<?>> getVariables();

}
