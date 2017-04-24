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
public interface Action extends Serializable {

	void execute();

	String getId();

	Map<Integer, ExchangeVariable> getOutputs();

	Map<Integer, ExchangeVariable> getScores();

}
