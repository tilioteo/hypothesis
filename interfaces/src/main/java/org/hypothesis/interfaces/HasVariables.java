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
public interface HasVariables extends Serializable {

	Map<String, Variable<?>> getVariables();

}
