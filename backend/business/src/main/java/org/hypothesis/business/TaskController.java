/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.util.Map;

import org.hypothesis.builder.Controller;
import org.hypothesis.data.model.Slide;
import org.hypothesis.data.model.Task;
import org.hypothesis.evaluation.Node;
import org.hypothesis.interfaces.Evaluator;
import org.hypothesis.interfaces.ExchangeVariable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface TaskController extends Controller, Evaluator {

	/**
	 * Add set of slide output variables
	 * 
	 * @param slide
	 *            the slide as origin of outputs
	 * @param outputValues
	 *            map of indexed output variables
	 */
	void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues);

	/**
	 * Look for node associated with slide in task and evaluate conditions to
	 * get next slide index.
	 * 
	 * @param task
	 *            processed task
	 * @param slide
	 *            processed slide which is a child of the task
	 * @return index of next slide - value >= 1 means direct index of slide in
	 *         task, 0 means next slide after currently processed, -1 means go
	 *         to next task
	 */
	int getNextSlideIndex(Task task, Slide slide);

	void addNode(Long slideId, Node node);

}