/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.util.Map;

import org.hypothesis.builder.Controller;
import org.hypothesis.data.model.Slide;
import org.hypothesis.evaluation.AbstractBasePath;
import org.hypothesis.interfaces.ExchangeVariable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface BranchController extends Controller {

	void addPath(AbstractBasePath path);

	void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues);

	String getNextBranchKey();

	String getNextKey();

}